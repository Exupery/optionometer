package models

import anorm.Row
import scala.math.BigDecimal

class BearPut(row: Row) extends TwoLegTrade(row) with Bearish with Puts {
  
  val lowerStrike = shortStrike
  val higherStrike = longStrike
  val maxLossAmount = longAsk - shortBid
  val maxProfitAmount = higherStrike - lowerStrike - maxLossAmount
  val maxProfitPrice = lowerStrike
  val maxLossPrice = higherStrike
  val breakevenPrice = higherStrike - maxLossAmount

}