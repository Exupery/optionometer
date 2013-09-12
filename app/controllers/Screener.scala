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
  
  def screenerWithParams(strat: String, und: String, moneyness: Option[String], minDays: Option[Int], maxDays: Option[Int]) = Action {
    val params = ScreenParams(strat, und, moneyness, minDays, maxDays)
    val strats = List("bullish", "bearish", "bullcalls", "bearcalls", "bullputs", "bearputs")
    val trades = if (strats.contains(strat)) {
      screen(ScreenParams(strat, und, moneyness, minDays, maxDays))
    } else {
      Seq("bullish", "bearish").foldLeft(List.empty[TwoLegTrade]) { (list, strategy) => 
        list ++ screen(ScreenParams(strategy, und, moneyness, minDays, maxDays))
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
      params.strat.toLowerCase match {
        case "bullcall" => new BullCall(row)
        case "bearcall" => new BearCall(row)
        case "bullput" => new BullPut(row)
        case "bearput" => new BearPut(row)
        case "bullish" => if (row[String]("callOrPut").equalsIgnoreCase("C")) new BullCall(row) else new BullPut(row)
        case "bearish" => if (row[String]("callOrPut").equalsIgnoreCase("C")) new BearCall(row) else new BearPut(row)
      }
	  }
	  return trades
  }
  
  def strikeClause(strat: String): String = {
    val opr = strat.toLowerCase match {
      case s if "bull.*".r.findFirstIn(s).isDefined => "<"
      case s if "bear.*".r.findFirstIn(s).isDefined => ">"
      case _ => "!="  
    }
    return "longStrike " + opr + " shortStrike"
  }
  
  def moneyClause(strat: String, moneyness: String): String = {
    moneyness.toLowerCase match {
      case "itm" => if (strat.startsWith("bull")) "undLast > shortStrike" else "undLast < shortStrike"
      case "otm" => if (strat.startsWith("bull")) "undLast < shortStrike" else "undLast > shortStrike"
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
  
  case class ScreenParams(strat: String, und: String, moneyness: Option[String]=None, minDays: Option[Int]=None, maxDays: Option[Int]=None) {
    val cookies = Seq(
        cookie("strat", strat), 
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