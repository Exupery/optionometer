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
	println(strat, und, moneyness, minDays, maxDays)	//DELME  
//    val u = "MSFT"	//TODO get from params
//    val cp = "C"	//TODO get from params
//	val qry = """
//	  	SELECT * FROM twolegs WHERE FROM_UNIXTIME(expires)>NOW() AND
//		underlier={underlier} AND
//		callOrPut={callOrPut} AND
//		longStrike<shortStrike LIMIT 5;
//	  """	//TODO: revmove limit
//	val sql = SQL(qry).on("underlier"->u, "callOrPut"->cp)
//	val base = "SELECT * FROM twolegs WHERE FROM_UNIXTIME(expires)>NOW()"
	val base = "SELECT * FROM twolegs WHERE FROM_UNIXTIME(expires)>NOW() AND " +
	  strikeAndType(strat) + " " + money(moneyness.getOrElse("any"))
	println(base)	//DELME
	val sql: SimpleSql[Row] = if (und.equalsIgnoreCase("all")) {
	  SQL(base + " LIMIT 25").asSimple
	} else {
	  SQL(base+" AND underlier={underlier} LIMIT 25").on("underlier"->und)
	}
    val trades: List[TwoLegTrade] = runQuery(sql).map { row =>
      new BullCall(row)	
	}
	return trades
  }
  
  def strikeAndType(strat: String): String = {
    strat.toLowerCase match {
	  case "bullcalls" => "longStrike < shortStrike AND callOrPut='C'"
	  case "bullputs" => "longStrike < shortStrike AND callOrPut='P'"
      case "bullish" => "longStrike < shortStrike"
	  case "bearcalls" => "longStrike > shortStrike AND callOrPut='C'"
	  case "bearputs" => "longStrike > shortStrike AND callOrPut='P'"
      case "bearish" => "longStrike > shortStrike"
	  case _ => "longStrike != shortStrike"
	}
  }
  
  def money(moneyness: String): String = {
    //TODO: handle itm/otm
    val between = moneyness.toLowerCase match {
      case "itm" => (0.75, 1.25)
      case "atm" => (0.975, 1.025)
      case "otm" => (0.75, 1.25)
      case _ => (0.75, 1.25)
      
    }
    return "AND longStrike BETWEEN (undLast*"+between._1+") AND (undLast*"+between._2+") "+
			"AND shortStrike BETWEEN (undLast*"+between._1+") AND (undLast*"+between._2+")"
  }
  
  def runQuery(sql: SimpleSql[Row]): List[Row] = {
    DB.withConnection(implicit c => sql().toList)
  }

}