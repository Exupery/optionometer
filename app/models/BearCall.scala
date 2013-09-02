package models

import anorm.Row
import scala.math.BigDecimal

class BearCall(row: Row) extends TwoLegTrade(row) {
  
  override def lowerStrike: BigDecimal = shortStrike
  override def higherStrike: BigDecimal = longStrike
  override def maxProfitAmount: BigDecimal = shortBid - longAsk
  override def maxLossAmount: BigDecimal = higherStrike - lowerStrike - maxProfitAmount
  override def maxProfitPrice: BigDecimal = lowerStrike
  override def maxLossPrice: BigDecimal = higherStrike
  override def breakevenPrice: BigDecimal = lowerStrike + maxProfitAmount

}