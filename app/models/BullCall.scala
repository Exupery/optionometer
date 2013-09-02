package models

import anorm.Row
import scala.math.BigDecimal

class BullCall(row: Row) extends TwoLegTrade(row) {
  
  override val lowerStrike: BigDecimal = longStrike
  override val higherStrike: BigDecimal = shortStrike
  override val maxProfitAmount: BigDecimal = higherStrike - lowerStrike - maxLossAmount
  override val maxLossAmount: BigDecimal = longAsk - shortBid
  override val maxProfitPrice: BigDecimal = higherStrike
  override val maxLossPrice: BigDecimal = lowerStrike
  override val breakevenPrice: BigDecimal = lowerStrike + maxLossAmount

}