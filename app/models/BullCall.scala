package models

import anorm.Row
import scala.math.BigDecimal

class BullCall(row: Row) extends TwoLegTrade(row) with Bullish with Calls {
  
  override def lowerStrike: BigDecimal = longStrike
  override def higherStrike: BigDecimal = shortStrike
  override def maxProfitAmount: BigDecimal = higherStrike - lowerStrike - maxLossAmount
  override def maxLossAmount: BigDecimal = longAsk - shortBid
  override def maxProfitPrice: BigDecimal = higherStrike
  override def maxLossPrice: BigDecimal = lowerStrike
  override def breakevenPrice: BigDecimal = lowerStrike + maxLossAmount

}