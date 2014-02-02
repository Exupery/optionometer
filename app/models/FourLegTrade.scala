package models

import anorm._

abstract class FourLegTrade(underlier: String, undLast: BigDecimal, expires: Long) 
	extends Trade(underlier, undLast, expires) {
  
  val lowerMaxProfitPrice: BigDecimal
  val higherMaxProfitPrice: BigDecimal
  val lowerMaxLossPrice: BigDecimal
  val higherMaxLossPrice: BigDecimal
  
}