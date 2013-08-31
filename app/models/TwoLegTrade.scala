package models

import anorm._
import java.math.{BigDecimal => JBD}

class TwoLegTrade(row: Row) {
  println(row)	//DELME
//   'ColumnName(options.bid,Some(longBid))':16.1500 as java.math.BigDecimal, 'ColumnName(options.ask,Some(longAsk))':19.4500 as java.math.BigDecimal, 'ColumnName(options.strike,Some(longStrike))':14.00 as java.math.BigDecimal, 
//  'ColumnName(options.symbol,Some(shortSym))':+MSFT-131019C19.00 as java.lang.String, 'ColumnName(options.bid,Some(shortBid))':12.5500 as java.math.BigDecimal, 'ColumnName(options.ask,Some(shortAsk))':12.9500 as java.math.BigDecimal, 'ColumnName(options.strike,Some(shortStrike))':19.00 as java.math.BigDecimal)

  val underlier = row[String]("underlier")
  val undLast = BigDecimal(row[JBD]("undLast"))
  val expires = row[Long]("expires")
  val longSym = row[String]("longSym")
  val longBid = BigDecimal(row[JBD]("longBid"))
  val longAsk = BigDecimal(row[JBD]("longAsk"))
  val longStrike = BigDecimal(row[JBD]("longStrike"))
  val shortSym = row[String]("shortSym")
  val shortBid = BigDecimal(row[JBD]("shortBid"))
  val shortAsk = BigDecimal(row[JBD]("shortAsk"))
  val shortStrike = BigDecimal(row[JBD]("shortStrike"))
}