package models

import anorm.Row
import scala.math.BigDecimal

class BullCall(row: Row) extends TwoLegTrade(row) with Bullish with Calls {
  
  val lowerStrike = longStrike
  val higherStrike = shortStrike
  val maxLossAmount = longAsk - shortBid
  val maxProfitAmount = higherStrike - lowerStrike - maxLossAmount
  val maxProfitPrice = higherStrike
  val maxLossPrice = lowerStrike
  val breakevenPrice = lowerStrike + maxLossAmount

}