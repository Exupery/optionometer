package controllers

import anorm._ 
import play.api._
import play.api.Play.current
import play.api.db.DB
import play.api.mvc._
import models._

object Screener extends Controller {
  
  def screenerNoParams = Action {
    Ok(views.html.screener()).withCookies(ScreenParams().cookies:_*)
  }
  
  def screenerWithParams(
      strategy: String, 
      und: String, 
      moneyness: Option[String], 
      minDays: Option[Int], 
      maxDays: Option[Int],
      minProfitPercent: Option[Int],
      minProfitAmount: Option[Int],
      maxLossAmount: Option[Int]) = Action {
    val strat = strategy match {
      case "bullcalls" => Strategy.BullCalls
      case "bearcalls" => Strategy.BearCalls
      case "bullputs" => Strategy.BullPuts
      case "bearputs" => Strategy.BearPuts
      case "bullish" => Strategy.AllBullish
      case "bearish" => Strategy.AllBearish
      case _ => Strategy.All
    }
    def params = ScreenParams(_: Strategy, und, moneyness, minDays, maxDays, minProfitPercent, minProfitAmount, maxLossAmount)
    val strats = if (strat.ne(Strategy.All)) Set(strat) else Set(Strategy.AllBullish, Strategy.AllBearish)
    val trades = {
      strats.foldLeft(List.empty[TwoLegTrade])((lst, strt) => lst ++ screen(params(strt)))
      .sortBy(_.score)(Ordering.Double.reverse)
      .splitAt(500)
    }
    Logger.debug(trades._1.size+" "+trades._2.size)			//DELME
    Ok(views.html.trades(trades._1)).withCookies(params(strat).cookies:_*)
  }
  
  def screen(params: ScreenParams): Set[TwoLegTrade] = {
    Logger.debug("*** SCREEN START ***") //DELME
		val limit = if (params.und.equalsIgnoreCase("all")) 2000 else	4000
		val query = {
		  "SELECT * FROM twolegs WHERE " +
		  strikeClause(params.strat) + " AND " +
		  moneyClause(params.strat, params.moneyness.getOrElse("any")) + " AND " + 
		  daysClause(params.minDays, params.maxDays)
		}
		val sql: SimpleSql[Row] = if (params.und.equalsIgnoreCase("all")) {
		  SQL(query + " LIMIT {limit}").on("limit"->limit)
		} else {
		  SQL(query+" AND underlier={underlier} LIMIT {limit}").on("underlier"->params.und, "limit"->limit)
		}
    val trades: List[TwoLegTrade] = runQuery(sql).map { row =>
      params.strat match {
        case Strategy.BullCalls => new BullCall(row)
        case Strategy.BearCalls => new BearCall(row)
        case Strategy.BullPuts => new BullPut(row)
        case Strategy.BearPuts => new BearPut(row)
        case Strategy.AllBullish => if (row[String]("callOrPut").equalsIgnoreCase("C")) new BullCall(row) else new BullPut(row)
        case Strategy.AllBearish => if (row[String]("callOrPut").equalsIgnoreCase("C")) new BearCall(row) else new BearPut(row)
      }
	  }
		Logger.debug("*** SCREEN COMPLETE ***") //DELME
    return filterResults(trades, params)
  }
  
  def filterResults(trades: List[TwoLegTrade], params: ScreenParams): Set[TwoLegTrade] = {
    val unrealisticPercent = BigDecimal(500)
    val unrealisticPercentPerDay = BigDecimal(15)
    val unrealisticDistancePercentPerDay = BigDecimal(7.5)
    val minSensibleAmount = BigDecimal(0.05)
    return trades.filter { trade =>
      trade.maxLossAmount < BigDecimal(params.maxLossAmount.getOrElse(9999)) &&
      trade.maxProfitAmount > BigDecimal(params.minProfitAmount.getOrElse(0)) &&
      trade.profitPercent >= BigDecimal(params.minProfitPercent.getOrElse(0)) &&
      trade.profitPercent < unrealisticPercent &&
      trade.profitPercentPerDay < unrealisticPercentPerDay &&
      trade.percentPerDayToMaxProfit < unrealisticDistancePercentPerDay &&
      trade.maxProfitAmount > minSensibleAmount
    }.toSet
  }
  
  def strikeClause(strat: Strategy): String = {
    val opr = strat match {
      case bullish if (strat.isBullish) => "<"
      case bearish if (strat.isBearish) => ">"
      case _ => "!="  
    }
    return "longStrike " + opr + " shortStrike"
  }
  
  def moneyClause(strat: Strategy, moneyness: String): String = {
    moneyness.toLowerCase match {
      case "itm" => if (strat.isBullish) "undLast > shortStrike" else "undLast < shortStrike"
      case "otm" => if (strat.isBullish) "undLast < shortStrike" else "undLast > shortStrike"
      case "ntm" => "shortStrike BETWEEN (undLast*0.99) AND (undLast*1.01)"
      case _ => "shortStrike BETWEEN (undLast*0.96) AND (undLast*1.04)"
    }
  }
  
  def daysClause(minDays: Option[Int], maxDays: Option[Int]): String = {
    val secondsInDay = 24 * 60 * 60
    val minSec = minDays.getOrElse(0) * secondsInDay
    val maxSec = maxDays.getOrElse(365) * secondsInDay
    val minExp = (System.currentTimeMillis / 1000) + minSec
    val maxExp = (System.currentTimeMillis / 1000) + maxSec
    return "expires BETWEEN " + minExp + " AND " + maxExp
  }
  
  def runQuery(sql: SimpleSql[Row]): List[Row] = {
    DB.withConnection(implicit c => sql().toList)
  }
  
  case class ScreenParams(
      strat: Strategy=Strategy.All, 
      und: String="all", 
      moneyness: Option[String]=None, 
      minDays: Option[Int]=None, 
      maxDays: Option[Int]=None,
      minProfitPercent: Option[Int]=None,
      minProfitAmount: Option[Int]=None,
      maxLossAmount: Option[Int]=None) {
    
    val cookies = Seq(
        cookie("strat", strat.toString), 
        cookie("sym", if (und.equalsIgnoreCase("all")) "" else und.toUpperCase), 
        cookie("moneyness", moneyness.getOrElse("any")),
        cookie("minDays", minDays.getOrElse(0).toString),
        cookie("maxDays", maxDays.getOrElse(0).toString),
        cookie("minProfitAmount", minProfitAmount.getOrElse(0).toString),
        cookie("minProfitPercent", minProfitPercent.getOrElse(0).toString),
        cookie("maxLossAmount", maxLossAmount.getOrElse(0).toString)
    )
    private def cookie(key: String, value: String): Cookie = {
      Cookie(key, value, None, routes.Screener.screenerNoParams.url, None, false, false)
    }
  }

}