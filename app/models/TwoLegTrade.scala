package models

import anorm._
import java.math.{BigDecimal => JBD}

abstract class TwoLegTrade(row: Row) 
	extends Trade(row[String]("underlier"), row[JBD]("undLast"), row[Long]("expires")) {
  
  val lowerStrike: BigDecimal
  val higherStrike: BigDecimal
  
  val longSym = row[String]("longSym")
  val longAsk = twoDigit(row[JBD]("longAsk"))
  val longStrike = twoDigit(row[JBD]("longStrike"))
  
  val shortSym = row[String]("shortSym")
  val shortBid = twoDigit(row[JBD]("shortBid"))
  val shortStrike = twoDigit(row[JBD]("shortStrike"))
  
  val longStrikes = List(longStrike)
  val shortStrikes = List(shortStrike)
  
  val legsFull = {
    val longLeg = "L" + longStrike + callOrPut
    val shortLeg = "S" + shortStrike + callOrPut
    if (longStrike < shortStrike) longLeg + " " + shortLeg else shortLeg + " " + longLeg 
  }
  val legsCompact = legsFull
  val comparator = underlier + longStrike + shortStrike + expires + row[String]("callOrPut")
  lazy val isItm = if (this.isInstanceOf[Bullish]) undLast > maxProfitPrice else undLast < maxProfitPrice
  lazy val isProfitable = if (this.isInstanceOf[Bullish]) undLast > breakevenPrice else undLast < breakevenPrice
  
}
