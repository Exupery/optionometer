package controllers

import java.math.{BigDecimal => JBD}
import anorm._ 
import play.api._
import play.api.Play.current
import play.api.db.DB
import play.api.mvc._
import models._

object Trades extends Controller {
  
  def tradesNoParams(und: String) = Action {
    Redirect(routes.Screener.screenerWithParams("all", und, None, None, None, None, None, None))
  }
  
  def trades(
      und: String,
      year: String,
      month: String,
      legs: String) = Action {
    //MSFT/2013/12/L34.00P-S35.00P
    println(und, year, month, legs)	//DELME
    val params = new TradeParams(und, year, month, legs)
    println(params.underlier, params.expiryYear, params.expiryMonth, params.legs)	//DELME
    val trade = twoLegTrade(params)
    println(trade) //DELME
    Ok
  }
  
  def twoLegTrade(params: TradeParams): Option[TwoLegTrade] = {
    val longLeg = if (params.legs(0).isLong) params.legs(0) else params.legs(1)
    val shortLeg = if (params.legs(0).isLong) params.legs(1) else params.legs(0)
    val callOrPut = if (longLeg.isCall) "C" else "P"
    val sql: SimpleSql[Row] = {
      SQL {
        "SELECT l.underlier, stocks.last_trade AS undLast, " + 
        "l.exp_unixtime AS expires, l.symbol AS longSym, l.bid AS longBid, l.call_or_put AS callOrPut, " + 
        "l.ask AS longAsk, l.strike AS longStrike, s.symbol AS shortSym, s.bid AS shortBid, " +
        "s.ask AS shortAsk, s.strike AS shortStrike " +
        "FROM options AS l JOIN options AS s ON l.underlier=s.underlier AND l.exp_unixtime=s.exp_unixtime " +
        "AND l.call_or_put=s.call_or_put JOIN stocks ON l.underlier=stocks.symbol " +
        "WHERE l.underlier={underlier} AND l.call_or_put={callOrPut} " +
        "AND l.exp_year={expYear} AND l.exp_month={expMonth} " +
        "AND l.strike={longStrike} AND s.strike={shortStrike} LIMIT 1"
      }.on("underlier"->params.underlier, 
          "callOrPut"->callOrPut,
          "expYear"->params.expiryYear,
          "expMonth"->params.expiryMonth,
          "longStrike"->new JBD(longLeg.strike.toString), 
          "shortStrike"->new JBD(shortLeg.strike.toString))
    }
    val rows = DB.withConnection(implicit c => sql().toList)
    return if (rows.nonEmpty) {
      val row = rows.head
	    Some(if (longLeg.isCall) {
	      if (longLeg.strike < shortLeg.strike) new BullCall(row) else new BullPut(row)
	    } else {
	      if (longLeg.strike < shortLeg.strike) new BearCall(row) else new BearPut(row)
	    })
    } else {
      None
    }
  }
  
  class TradeParams(
      und: String,
      year: String,
      month: String,
      legStr: String) {
    val underlier = und.toUpperCase
    val expiryYear: Int = safeInt(year)
    val expiryMonth: Int = safeInt(month)
    val legs: List[Leg] = parseLegs(legStr)
    
    private def parseLegs(str: String): List[Leg] = {
      return str.toUpperCase.split("-").filter(_.length>2).map { leg =>
        val strike: BigDecimal = safeBigDecimal(leg.substring(1, leg.length-1))
        Leg(leg.startsWith("L"), strike, leg.endsWith("C"))
      }.toList
    }
    
    private def safeInt(str: String): Int = {
      try {
        str.toInt
      } catch {
        case e: NumberFormatException => 0
      }
    }
    
    private def safeBigDecimal(str: String): BigDecimal = {
      try {
        BigDecimal(str)
      } catch {
        case e: NumberFormatException => BigDecimal("0")
      }
    }
  }
  
  case class Leg(isLong: Boolean, strike: BigDecimal, isCall: Boolean)

}