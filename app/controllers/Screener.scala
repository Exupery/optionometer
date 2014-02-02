package controllers

import anorm._ 
import play.api._
import play.api.Play.current
import play.api.db.DB
import play.api.mvc._
import java.math.{BigDecimal => JBD}
import models._

object Screener extends Controller {
  
  def screenerNoParams = Action {
    Ok(views.html.screener()).withCookies(ScreenParams().cookies:_*)
  }
  
  def screener(
      strategy: String, 
      unds: String, 
      moneyness: Option[String], 
      minDays: Option[Int], 
      maxDays: Option[Int],
      minProfitPercent: Option[Int],
      minProfitAmount: Option[Int],
      maxLossAmount: Option[Int]) = Action {
    val strat = strategy match {
      case "bullcalls" => BullCalls
      case "bearcalls" => BearCalls
      case "bullputs" => BullPuts
      case "bearputs" => BearPuts
      case "longcallbutterflies" => LongCallButterflies
      case "longputbutterflies" => LongPutButterflies
      case "bullish" => AllBullish
      case "bearish" => AllBearish
      case "rangebound" => AllRangebound
      case _ => All
    }

    def sp = ScreenParams(_: Strategy, unds.split("[, ]"), moneyness, minDays, maxDays, minProfitPercent, minProfitAmount, maxLossAmount)
    val params = {
	    strat match {
	      case All => List(sp(AllBullish), sp(AllBearish), sp(LongCallButterflies), sp(LongPutButterflies))
	      case AllRangebound => List(sp(LongCallButterflies), sp(LongPutButterflies))
	      case _ => List(sp(strat))
	    }
	  }
    val trades = {
      params.foldLeft(List.empty[Trade])((list, stratParams) => list ++ screen(stratParams))
	      .sortBy(_.score)(Ordering.Double.reverse)
	      .splitAt(500)
    }
    Logger.debug(trades._1.size+" "+trades._2.size)			//DELME
    Ok(views.html.screener(Some(trades._1))).withCookies(sp(strat).cookies:_*)
  }
  
  def screen(params: ScreenParams): Set[Trade] = {
    return filterResults(params.strat match {
      case LongCallButterflies | LongPutButterflies => screenFourLeg(params)
      case _ => screenTwoLeg(params)
    }, params)
  }
  
  def screenFourLeg(params: ScreenParams): List[FourLegTrade] = {
    val isCalls = params.strat.isInstanceOf[Calls]
    val bulls = screenTwoLeg(params, Some(if (isCalls) BullCalls else BullPuts)).groupBy(_.underlier) 
    val bears = screenTwoLeg(params, Some(if (isCalls) BearCalls else BearPuts)).groupBy(_.underlier)
    
    val trades = bulls.map { case (und, bullTrades) =>
      bullTrades.map { bull =>
        val diff = bull.higherStrike - bull.lowerStrike
      	val bearTrades = bears.getOrElse(und, List()).filter { bear => 
      	  (bear.lowerStrike == bull.higherStrike) &&
      	  (bear.expires == bull.expires) &&
      	  (bear.higherStrike - bear.lowerStrike == diff)
        }
        
      	if (isCalls) {
      		bearTrades.map(bear => new LongCallButterfly(bull.asInstanceOf[BullCall], bear.asInstanceOf[BearCall]))
      	} else {
      	  bearTrades.map(bear => new LongPutButterfly(bull.asInstanceOf[BullPut], bear.asInstanceOf[BearPut]))
      	}
      }.flatten
    }.flatten
    
    return (params.moneyness match {
      case Some("itm") | Some("atm") => trades.filter(_.isItm)
      case Some("otm") => trades.filterNot(_.isItm)
      case _ => trades
    }).toList
  }
  
  def screenTwoLeg(params: ScreenParams, overrideStrat: Option[Strategy]=None): List[TwoLegTrade] = {
    Logger.debug("*** SCREEN START ***") //DELME
    val strat = overrideStrat.getOrElse(params.strat)
		val limit = if (params.isAll) 2000 else if (params.underliers.size<10) 4000 else 1000
		val query = {
		  "SELECT * FROM twolegs WHERE " + strikeClause(strat) + " AND " + 
		  (if (strat.isInstanceOf[Calls] || strat.isInstanceOf[Puts]) callOrPutClause(strat) + " AND " else "") + 
		  (if (overrideStrat.isEmpty) moneyClause(strat, params.moneyness.getOrElse("any")) + " AND " else "") + 
		  daysClause(params.minDays, params.maxDays)
		}
    //Iterating through underliers due to anorm's lack of SQL IN support
    val trades: List[TwoLegTrade] = params.underliers.foldLeft(List.empty[TwoLegTrade]) { (trades, und) =>
			val sql: SimpleSql[Row] = if (params.isAll) {
			  SQL(query + " LIMIT {limit}").on("limit"->limit)
			} else {
			  SQL(query+" AND underlier={underlier} LIMIT {limit}").on("underlier"->und.toUpperCase, "limit"->limit)
			}
	    trades ++ runQuery(sql).filter(r=>BigDecimal(r[JBD]("shortbid"))>0.05).map { row =>
	      strat match {
	        case BullCalls => new BullCall(row)
	        case BearCalls => new BearCall(row)
	        case BullPuts => new BullPut(row)
	        case BearPuts => new BearPut(row)
	        case AllBullish => if (row[String]("callOrPut").equalsIgnoreCase("C")) new BullCall(row) else new BullPut(row)
	        case AllBearish => if (row[String]("callOrPut").equalsIgnoreCase("C")) new BearCall(row) else new BearPut(row)
	      }
		  }
    }
		Logger.debug("*** SCREEN COMPLETE ***") //DELME
    return trades
  }
  
  def filterResults(trades: List[Trade], params: ScreenParams): Set[Trade] = {
    val unrealisticDistancePercentPerDay = BigDecimal(6.5)
    val minSensibleAmount = BigDecimal(0.05)
    return trades.filter { trade =>
      trade.maxLossAmount < BigDecimal(params.maxLossAmount.getOrElse(9999)) &&
      trade.maxProfitAmount > BigDecimal(params.minProfitAmount.getOrElse(0)) &&
      trade.profitPercent >= BigDecimal(params.minProfitPercent.getOrElse(0)) &&
      trade.profitPercent < params.strat.unrealisticPercent &&
      trade.profitPercentPerDay < params.strat.unrealisticPercentPerDay &&
      trade.percentPerDayToMaxProfit < unrealisticDistancePercentPerDay &&
      trade.maxProfitAmount > minSensibleAmount
    }.toSet
  }
  
  def callOrPutClause(strat: Strategy): String = {
    if (strat.isInstanceOf[Calls]) {
      "callOrPut='C'"
    } else if (strat.isInstanceOf[Puts]) {
      "callOrPut='P'"
    } else {
      ""
    }
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
  
  def runQuery(sql: SimpleSql[Row]): List[Row] = DB.withConnection { implicit c =>
    sql().toList
  }
  
  case class ScreenParams(
      strat: Strategy=Strategy.All, 
      underliers: Seq[String]=Seq("all"), 
      moneyness: Option[String]=None, 
      minDays: Option[Int]=None, 
      maxDays: Option[Int]=None,
      minProfitPercent: Option[Int]=None,
      minProfitAmount: Option[Int]=None,
      maxLossAmount: Option[Int]=None) {
    
    val isAll = underliers.isEmpty || underliers.head.equalsIgnoreCase("all")
    
    val cookies = Seq(
        cookie("strat", strat.toString), 
        cookie("sym", if (isAll) "" else underliers.mkString(" ").toUpperCase),
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