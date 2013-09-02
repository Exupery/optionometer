package models

import anorm.Row
import scala.math.BigDecimal

class BearPut(row: Row) extends TwoLegTrade(row) {
  
  override val lowerStrike: BigDecimal = shortStrike
  override val higherStrike: BigDecimal = longStrike
  override val maxProfitAmount: BigDecimal = higherStrike - lowerStrike - maxLossAmount
  override val maxLossAmount: BigDecimal = longBid - shortAsk
  override val maxProfitPrice: BigDecimal = lowerStrike
  override val maxLossPrice: BigDecimal = higherStrike
  override val breakevenPrice: BigDecimal = higherStrike - maxLossAmount

}