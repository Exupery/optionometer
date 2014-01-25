package models

import java.math.{BigDecimal => JBD}
import java.text.SimpleDateFormat
import java.util.Date

abstract case class Trade(underlier: String, last: BigDecimal, expires: Long) {
  
  val maxProfitAmount: BigDecimal
  val maxLossAmount: BigDecimal
  val maxProfitPrice: BigDecimal
  val maxLossPrice: BigDecimal
  val breakevenPrice: BigDecimal
  val comparator: String
  val isItm: Boolean
  
  val undLast = twoDigit(last)
  val callOrPut = if (this.isInstanceOf[Calls]) "C" else "P"
  val daysToExpire = ((expires - (System.currentTimeMillis / 1000)) / (60 * 60 *24)).toInt
  val expMonthYear = {
    val df = new SimpleDateFormat("dd MMM yyyy")
    df.format(new Date(expires * 1000))
  }
  
  lazy val profitPercent = twoDigit(if (maxLossAmount != 0) maxProfitAmount / maxLossAmount * 100 else 0)
  lazy val profitPercentPerDay = twoDigit(if (daysToExpire > 0) profitPercent / daysToExpire else profitPercent)
  lazy val amountToMaxProfit = maxProfitPrice - undLast
  lazy val amountToMaxLoss = maxLossPrice - undLast
  lazy val amountToBreakeven = breakevenPrice - undLast 
  lazy val percentToMaxProfit = twoDigit(amountToMaxProfit / undLast * 100)
  lazy val percentToMaxLoss = twoDigit(amountToMaxLoss / undLast * 100)
  lazy val percentToBreakeven = twoDigit(amountToBreakeven / undLast * 100)
  lazy val percentPerDayToMaxProfit = twoDigit(if (daysToExpire > 0) percentToMaxProfit / daysToExpire else percentToMaxProfit)
  lazy val percentPerDayToMaxLoss = twoDigit(if (daysToExpire > 0) percentToMaxLoss / daysToExpire else percentToMaxLoss)
  lazy val percentPerDayToBreakeven = twoDigit(if (daysToExpire > 0) percentToBreakeven / daysToExpire else percentToBreakeven)
  lazy val score: Double = {
		val profitDist = if (this.isInstanceOf[Bullish]) (-percentPerDayToMaxProfit) else percentPerDayToMaxProfit
    (profitPercentPerDay * profitDist).toDouble
  }
  
  def twoDigit(bigDec: BigDecimal): BigDecimal = bigDec.setScale(2, BigDecimal.RoundingMode.HALF_UP)
  
  override def hashCode: Int = comparator.hashCode
  
  override def equals(that: Any): Boolean = {
    that match {
      case trade: Trade => comparator.equalsIgnoreCase(trade.comparator)
      case _ => false
    }
  }
  
}

trait Bullish {}
trait Bearish {}
trait Rangebound {}
trait Calls {}
trait Puts {}