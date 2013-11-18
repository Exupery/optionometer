package controllers

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
    Ok
  }
  
  class TradeParams(
      und: String,
      year: String,
      month: String,
      legStr: String) {
    val underlier = und
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
  
  case class Leg(isLong: Boolean, strike: BigDecimal, callOrPut: Boolean)

}