package models

import anorm._
import java.math.{BigDecimal => JBD}

abstract class TwoLegTrade(row: Row) {
  
  val underlier = row[String]("underlier")
  val undLast = twoDigit(row[JBD]("undLast"))
  
  val longSym = row[String]("longSym")
  val longBid = twoDigit(row[JBD]("longBid"))
  val longAsk = twoDigit(row[JBD]("longAsk"))
  val longStrike = twoDigit(row[JBD]("longStrike"))
  
  val shortSym = row[String]("shortSym")
  val shortBid = twoDigit(row[JBD]("shortBid"))
  val shortAsk = twoDigit(row[JBD]("shortAsk"))
  val shortStrike = twoDigit(row[JBD]("shortStrike"))
  
  val expires = row[Long]("expires")
  val daysToExpire: Int = ((expires - (System.currentTimeMillis / 1000)) / (60 * 60 *24)).toInt
  
  val profitPercent = twoDigit(if (maxProfitAmount != 0) maxLossAmount / maxProfitAmount * 100 else 0)
  val profitPercentPerDay = twoDigit(if (daysToExpire != 0) profitPercent / daysToExpire else profitPercent)
  val amountToMaxProfit = maxProfitPrice - undLast
  val amountToMaxLoss = maxLossPrice - undLast
  val amountToBreakeven = breakevenPrice - undLast 
  val percentToMaxProfit = twoDigit(amountToMaxProfit / undLast * 100)
  val percentToMaxLoss = twoDigit(amountToMaxLoss / undLast * 100)
  val percentToBreakeven = twoDigit(amountToBreakeven / undLast * 100)
  val percentPerDayToMaxProfit = twoDigit(if (daysToExpire != 0) percentToMaxProfit / daysToExpire else percentToMaxProfit)
  val percentPerDayToMaxLoss = twoDigit(if (daysToExpire != 0) percentToMaxLoss / daysToExpire else percentToMaxLoss)
  val percentPerDayToBreakeven = twoDigit(if (daysToExpire != 0) percentToBreakeven / daysToExpire else percentToBreakeven)
  
  
  def maxProfitAmount: BigDecimal
  def maxLossAmount: BigDecimal
  def maxProfitPrice: BigDecimal
  def maxLossPrice: BigDecimal
  def breakevenPrice: BigDecimal
  def lowerStrike: BigDecimal
  def higherStrike: BigDecimal
  
  def twoDigit(bigDec: BigDecimal): BigDecimal = bigDec.setScale(2, BigDecimal.RoundingMode.HALF_UP)
  
}