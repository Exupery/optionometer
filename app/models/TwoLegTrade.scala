package models

import anorm._
import java.math.{BigDecimal => JBD}
import java.text.SimpleDateFormat
import java.util.Date

abstract class TwoLegTrade(row: Row) {
  
  val underlier = row[String]("underlier")
  val undLast = twoDigit(row[JBD]("undLast"))
  
  val longSym = row[String]("longSym")
  val longAsk = twoDigit(row[JBD]("longAsk"))
  val longStrike = twoDigit(row[JBD]("longStrike"))
  
  val shortSym = row[String]("shortSym")
  val shortBid = twoDigit(row[JBD]("shortBid"))
  val shortStrike = twoDigit(row[JBD]("shortStrike"))
  
  val expires = row[Long]("expires")
  val daysToExpire: Int = ((expires - (System.currentTimeMillis / 1000)) / (60 * 60 *24)).toInt
  val expMonthYear = {
    val df = new SimpleDateFormat("dd MMM yyyy")
    df.format(new Date(expires * 1000))
  }
  
  val comparator: String = underlier + longStrike + shortStrike + expires + row[String]("callOrPut")
  val profitPercent = twoDigit(if (maxLossAmount != 0) maxProfitAmount / maxLossAmount * 100 else 0)
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

  lazy val score: Double = {
		val profitDist = if (this.isInstanceOf[Bullish]) (-percentPerDayToMaxProfit) else percentPerDayToMaxProfit
    (profitPercentPerDay * profitDist).toDouble
  }
  
  def maxProfitAmount: BigDecimal
  def maxLossAmount: BigDecimal
  def maxProfitPrice: BigDecimal
  def maxLossPrice: BigDecimal
  def breakevenPrice: BigDecimal
  def lowerStrike: BigDecimal
  def higherStrike: BigDecimal
  
  def twoDigit(bigDec: BigDecimal): BigDecimal = bigDec.setScale(2, BigDecimal.RoundingMode.HALF_UP)
  
  override def toString: String = "L:" + longSym + "\tS:" + shortSym + "\tlast: " + undLast + "\tscore: " + score
  
  override def hashCode: Int = comparator.hashCode
  
  override def equals(that: Any): Boolean = {
    that match {
      case tlt: TwoLegTrade => comparator.equalsIgnoreCase(tlt.comparator)
      case _ => false
    }
  }
  
}

trait Bullish {}
trait Bearish {}