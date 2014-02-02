package controllers

import java.math.{BigDecimal => JBD}
import anorm._ 
import play.api._
import play.api.Play.current
import play.api.db.DB
import play.api.mvc._
import com.github.nscala_time.time.Imports._
import models._

object Trades extends Controller with SafeCast {
  
  def tradesNoLegs(und: String, year: String, month: String) = Action {
    val days = minMaxDays(safeInt(year), safeInt(month))
    Redirect(routes.Screener.screener("all", und, None, days._1, days._2, None, None, None))
  }
  
  def minMaxDays(year: Int, month: Int): (Option[Int], Option[Int]) = {
    val now = DateTime.now
    val curYear = now.getYear
    val year4d = if (year < 100) year + 2000 else year
    val minMax = if (year4d > curYear || (year4d == curYear && month >= now.getMonthOfYear)) {
      val startMonth = if (month > 0 && month <= 12) month else (if (year4d == curYear) now.getMonthOfYear else 1)
      val expRangeStart = new DateTime(year4d, startMonth, 1, 0, 0)
      val expRangeEnd = if (month > 0 && month <= 12) expRangeStart.plusMonths(1) else new DateTime(year4d, 12, 31, 0, 0)
      val millisInDay = 1000 * 60 * 60 * 24
      val minDays = (expRangeStart.millis - now.millis) / millisInDay 
    	val maxDays = (expRangeEnd.millis - now.millis) / millisInDay
    	(Some(minDays.toInt), Some(maxDays.toInt))
    } else {
      (None, None)
    }
    return minMax
  }
  
  def trades(und: String, year: String, month: String, legs: String) = Action {
    val params = new TradeParams(und, year, month, legs)
    val trade = if (params.legs.size==2) twoLegTrade(params) else if (params.legs.size==4) fourLegTrade(params) else None
    Ok(views.html.trades(trade))
  }
  
  def twoLegTrade(params: TradeParams): Option[TwoLegTrade] = {
    val longLeg = if (params.legs(0).isLong) params.legs(0) else params.legs(1)
    val shortLeg = if (params.legs(0).isLong) params.legs(1) else params.legs(0)
    return twoLegTrade(params, longLeg, shortLeg)
  }
  
  def twoLegTrade(params: TradeParams, longLeg: Leg, shortLeg: Leg): Option[TwoLegTrade] = {
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
	      if (longLeg.strike < shortLeg.strike) new BullCall(row) else new BearCall(row)
	    } else {
	      if (longLeg.strike < shortLeg.strike) new BullPut(row) else new BearPut(row)
	    })
    } else {
      None
    }
  }
  
  def fourLegTrade(params: TradeParams): Option[FourLegTrade] = {
    val longLegA = if (params.legs(0).isLong) params.legs(0) else params.legs(1)
    val shortLegA = if (params.legs(0).isLong) params.legs(1) else params.legs(0)
    val tradeA = twoLegTrade(params, longLegA, shortLegA)
    val longLegB = if (params.legs(2).isLong) params.legs(2) else params.legs(3)
    val shortLegB = if (params.legs(2).isLong) params.legs(3) else params.legs(2)
    val tradeB = twoLegTrade(params, longLegB, shortLegB)
    return if (tradeA.isDefined && tradeB.isDefined) {
      tradeA.get match {
        case a: BullCall => Some(new LongCallButterfly(a, tradeB.get.asInstanceOf[BearCall]))
        case a: BearCall => Some(new LongCallButterfly(tradeB.get.asInstanceOf[BullCall], a))
        case a: BullPut => Some(new LongPutButterfly(a, tradeB.get.asInstanceOf[BearPut]))
        case a: BearPut => Some(new LongPutButterfly(tradeB.get.asInstanceOf[BullPut], a))
        case _ => None
      }
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
    
  }
  
  case class Leg(isLong: Boolean, strike: BigDecimal, isCall: Boolean)

}

trait SafeCast {
  
	def safeInt(str: String): Int = {
    try {
      str.toInt
    } catch {
      case e: NumberFormatException => 0
    }
  }
  
  def safeBigDecimal(str: String): BigDecimal = {
    try {
      BigDecimal(str)
    } catch {
      case e: NumberFormatException => BigDecimal("0")
    }
  }
  
}