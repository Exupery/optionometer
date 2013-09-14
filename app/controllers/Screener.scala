package controllers

import anorm._ 
import play.api._
import play.api.Play.current
import play.api.db.DB
import play.api.mvc._
import models._

object Screener extends Controller {
  
  def screenerNoParams = Action {
    Ok(views.html.screener())
  }
  
  def screenerWithParams(strategy: String, und: String, moneyness: Option[String], minDays: Option[Int], maxDays: Option[Int]) = Action {
    val strat = strategy match {
      case "bullcalls" => Strategy.BullCalls
      case "bearcalls" => Strategy.BearCalls
      case "bullputs" => Strategy.BullPuts
      case "bearputs" => Strategy.BearPuts
      case "bullish" => Strategy.AllBullish
      case "bearish" => Strategy.AllBearish
      case _ => Strategy.All
    }
    val params = ScreenParams(strat, und, moneyness, minDays, maxDays)
    val trades = if (strat.ne(Strategy.All)) {
      screen(ScreenParams(strat, und, moneyness, minDays, maxDays))
    } else {
      Seq(Strategy.AllBullish, Strategy.AllBearish).foldLeft(List.empty[TwoLegTrade]) { (l, s) => 
        l ++ screen(ScreenParams(s, und, moneyness, minDays, maxDays))
      }
    }
    println(trades.size)			//DELME
    Ok(views.html.trades(trades)).withCookies(params.cookies:_*)
  }
  
  def screen(params: ScreenParams): List[TwoLegTrade] = {
		val limit = 12	//TODO: adjust limit
		val query = {
		  "SELECT * FROM twolegs WHERE " +
		  strikeClause(params.strat) + " AND " +
		  moneyClause(params.strat, params.moneyness.getOrElse("any")) + " AND " + 
		  daysClause(params.minDays, params.maxDays)
		}
		println(params)	//DELME 
		println(query)	//DELME
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
	  return trades
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
      case "ntm" => "shortStrike BETWEEN (undLast*0.975) AND (undLast*1.025)"
      case _ => "shortStrike BETWEEN (undLast*0.75) AND (undLast*1.25)"
    }
  }
  
  def daysClause(minDays: Option[Int], maxDays: Option[Int]): String = {
    val secondsInDay = 24 * 60 * 60
    val minSec = minDays.getOrElse(0) * secondsInDay
    val maxSec = maxDays.getOrElse(999) * secondsInDay
    val minExp = (System.currentTimeMillis / 1000) + minSec
    val maxExp = (System.currentTimeMillis / 1000) + maxSec
    return "expires BETWEEN " + minExp + " AND " + maxExp
  }
  
  def runQuery(sql: SimpleSql[Row]): List[Row] = {
    DB.withConnection(implicit c => sql().toList)
  }
  
  case class ScreenParams(strat: Strategy, und: String, moneyness: Option[String]=None, minDays: Option[Int]=None, maxDays: Option[Int]=None) {
    val cookies = Seq(
        cookie("strat", strat.toString), 
        cookie("sym", und), 
        cookie("moneyness", moneyness.getOrElse("any")),
        cookie("minDays", minDays.getOrElse(0).toString),
        cookie("maxDays", maxDays.getOrElse(0).toString)
    )
    private def cookie(key: String, value: String): Cookie = {
      Cookie(key, value, None, "/screener", None, false, false)
    }
  }

}