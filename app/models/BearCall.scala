package models

import anorm.Row
import scala.math.BigDecimal

class BearCall(row: Row) extends TwoLegTrade(row) {
  
  override val lowerStrike: BigDecimal = shortStrike
  override val higherStrike: BigDecimal = longStrike
  override val maxProfitAmount: BigDecimal = shortBid - longAsk
  override val maxLossAmount: BigDecimal = higherStrike - lowerStrike - maxProfitAmount
  override val maxProfitPrice: BigDecimal = lowerStrike
  override val maxLossPrice: BigDecimal = higherStrike
  override val breakevenPrice: BigDecimal = lowerStrike + maxProfitAmount

}