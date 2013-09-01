package models

import anorm.Row
import scala.math.BigDecimal

class BullCall(row: Row) extends TwoLegTrade(row) {
  
  def profitAmount: BigDecimal = shortStrike - longStrike - cost
  def amountToMaxProfit: BigDecimal = shortStrike - undLast
  def amountToMaxLoss: BigDecimal = longStrike - undLast
  def amountToBreakeven: BigDecimal = longStrike + cost - undLast 

}