package models

import anorm.Row
import scala.math.BigDecimal

class BullPut(row: Row) extends TwoLegTrade(row) {
  
  override val lowerStrike: BigDecimal = longStrike
  override val higherStrike: BigDecimal = shortStrike
  override def maxProfitAmount: BigDecimal = shortBid - longAsk
  override def maxLossAmount: BigDecimal = higherStrike - lowerStrike - maxProfitAmount
  override def maxProfitPrice: BigDecimal = higherStrike
  override def maxLossPrice: BigDecimal = lowerStrike
  override def breakevenPrice: BigDecimal = higherStrike - maxProfitAmount

}