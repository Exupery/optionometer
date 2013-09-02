package models

import anorm.Row
import scala.math.BigDecimal

class BullCall(row: Row) extends TwoLegTrade(row) {
  
  override val lowerStrike: BigDecimal = longStrike
  override val higherStrike: BigDecimal = shortStrike
  override def maxProfitAmount: BigDecimal = higherStrike - lowerStrike - maxLossAmount
  override def maxLossAmount: BigDecimal = longAsk - shortBid
  override def maxProfitPrice: BigDecimal = higherStrike
  override def maxLossPrice: BigDecimal = lowerStrike
  override def breakevenPrice: BigDecimal = lowerStrike + maxLossAmount

}