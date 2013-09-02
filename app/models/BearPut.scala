package models

import anorm.Row
import scala.math.BigDecimal

class BearPut(row: Row) extends TwoLegTrade(row) {
  
  override def lowerStrike: BigDecimal = shortStrike
  override def higherStrike: BigDecimal = longStrike
  override def maxProfitAmount: BigDecimal = higherStrike - lowerStrike - maxLossAmount
  override def maxLossAmount: BigDecimal = longBid - shortAsk
  override def maxProfitPrice: BigDecimal = lowerStrike
  override def maxLossPrice: BigDecimal = higherStrike
  override def breakevenPrice: BigDecimal = higherStrike - maxLossAmount

}