package models

import anorm.Row
import scala.math.BigDecimal

class BearPut(row: Row) extends TwoLegTrade(row) {
  
  override def maxProfitAmount: BigDecimal = longStrike - shortStrike - maxLossAmount
  override def maxLossAmount: BigDecimal = longBid - shortAsk
  override def maxProfitPrice: BigDecimal = longStrike
  override def maxLossPrice: BigDecimal = shortStrike
  override def breakevenPrice: BigDecimal = shortStrike - maxLossAmount

}