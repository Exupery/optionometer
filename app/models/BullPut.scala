package models

import anorm.Row
import scala.math.BigDecimal

class BullPut(row: Row) extends TwoLegTrade(row) {
  
  override val lowerStrike: BigDecimal = longStrike
  override val higherStrike: BigDecimal = shortStrike
  override def maxProfitAmount: BigDecimal = shortBid - longAsk
  override def maxLossAmount: BigDecimal = shortStrike - longStrike - maxProfitAmount
  override def maxProfitPrice: BigDecimal = shortStrike
  override def maxLossPrice: BigDecimal = longStrike
  override def breakevenPrice: BigDecimal = shortStrike - maxProfitAmount

}