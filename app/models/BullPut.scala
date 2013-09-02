package models

import anorm.Row
import scala.math.BigDecimal

class BullPut(row: Row) extends TwoLegTrade(row) {
  
  override val lowerStrike: BigDecimal = longStrike
  override val higherStrike: BigDecimal = shortStrike
  override val maxProfitAmount: BigDecimal = shortBid - longAsk
  override val maxLossAmount: BigDecimal = higherStrike - lowerStrike - maxProfitAmount
  override val maxProfitPrice: BigDecimal = higherStrike
  override val maxLossPrice: BigDecimal = lowerStrike
  override val breakevenPrice: BigDecimal = higherStrike - maxProfitAmount

}