package models

import anorm.Row
import scala.math.BigDecimal

class BearCall(row: Row) extends TwoLegTrade(row) with Bearish with Calls {
  
  val lowerStrike = shortStrike
  val higherStrike = longStrike
  val maxProfitAmount = shortBid - longAsk
  val maxLossAmount = higherStrike - lowerStrike - maxProfitAmount
  val maxProfitPrice = lowerStrike
  val maxLossPrice = higherStrike
  val breakevenPrice = lowerStrike + maxProfitAmount

}