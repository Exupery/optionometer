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
    val trades = screen(strat, und, moneyness, minDays, maxDays)
    Ok(views.html.trades(trades))
  }
  
  def screen(strat: String, und: String, moneyness: Option[String]=None, minDays: Option[Int]=None, maxDays: Option[Int]=None): List[TwoLegTrade] = {
	val limit = 25	//TODO: adjust limit
    println(strat, und, moneyness, minDays, maxDays)	//DELME  
	val query = {
	  "SELECT * FROM twolegs WHERE FROM_UNIXTIME(expires)>NOW() AND " +
	  strikeClause(strat.toLowerCase) + " AND " +
	  money(strat.toLowerCase, moneyness.getOrElse("any"))
	}
	println(query)	//DELME
	val sql: SimpleSql[Row] = if (und.equalsIgnoreCase("all")) {
	  SQL(query + " LIMIT {limit}").on("limit"->limit)
	} else {
	  SQL(query+" AND underlier={underlier} LIMIT {limit}").on("underlier"->und, "limit"->limit)
	}
    val trades: List[TwoLegTrade] = runQuery(sql).map { row =>
      new BullCall(row)	
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
  
  def money(strat: String, moneyness: String): String = {
    moneyness.toLowerCase match {
      case "itm" => if (strat.startsWith("bull")) "undLast > shortStrike" else "undLast < shortStrike"
      case "otm" => if (strat.startsWith("bull")) "undLast < shortStrike" else "undLast > shortStrike"
      case "ntm" => "shortStrike BETWEEN (undLast*0.975) AND (undLast*1.025)"
      case _ => "shortStrike BETWEEN (undLast*0.75) AND (undLast*1.25)"
      
    }
  }
  
  def runQuery(sql: SimpleSql[Row]): List[Row] = {
    DB.withConnection(implicit c => sql().toList)
  }

}