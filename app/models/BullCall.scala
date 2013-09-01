package models

import anorm.Row
import scala.math.BigDecimal

class BullCall(row: Row) extends TwoLegTrade(row) {
  
  override def maxProfitAmount: BigDecimal = shortStrike - longStrike - maxLossAmount
  override def maxLossAmount: BigDecimal = longAsk - shortBid
  override def maxProfitPrice: BigDecimal = shortStrike
  override def maxLossPrice: BigDecimal = longStrike
  override def breakevenPrice: BigDecimal = longStrike + maxLossAmount

}