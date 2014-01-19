package models

import anorm._

abstract class FourLegTrade(row: Row) extends Trade(row) {
  
  val comparator: String					//TODO
  val maxProfitAmount: BigDecimal	//TODO
  val maxLossAmount: BigDecimal		//TODO
  val maxProfitPrice: BigDecimal	//TODO
  val maxLossPrice: BigDecimal		//TODO
  val breakevenPrice: BigDecimal	//TODO
  val isItm: Boolean							//TODO
    
  override def toString: String //TODO

}