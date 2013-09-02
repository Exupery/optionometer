package models

import anorm.Row
import scala.math.BigDecimal

class BullCall(row: Row) extends TwoLegTrade(row) {
  
  override val lowerStrike: BigDecimal = longStrike
  override val higherStrike: BigDecimal = shortStrike
  override def maxProfitAmount: BigDecimal = shortStrike - longStrike - maxLossAmount
  override def maxLossAmount: BigDecimal = longAsk - shortBid
  override def maxProfitPrice: BigDecimal = shortStrike
  override def maxLossPrice: BigDecimal = longStrike
  override def breakevenPrice: BigDecimal = longStrike + maxLossAmount

}