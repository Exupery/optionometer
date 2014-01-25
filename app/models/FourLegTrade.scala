package models

import anorm._

abstract class FourLegTrade(underlier: String, undLast: BigDecimal, expires: Long) 
	extends Trade(underlier, undLast, expires) {
  
}