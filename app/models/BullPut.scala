package models

import anorm.Row
import scala.math.BigDecimal

class BullPut(row: Row) extends TwoLegTrade(row) with Bullish with Puts {
  
  val lowerStrike = longStrike
  val higherStrike = shortStrike
  val maxProfitAmount = shortBid - longAsk
  val maxLossAmount = higherStrike - lowerStrike - maxProfitAmount
  val maxProfitPrice = higherStrike
  val maxLossPrice = lowerStrike
  val breakevenPrice = higherStrike - maxProfitAmount

}