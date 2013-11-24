package views.helpers

import models._

object TradesHelper {
  
  def details(trade: TwoLegTrade): List[(String, Any)] = {
    val callOrPut = if (trade.isInstanceOf[Calls]) "Call" else "Put"
    return List(
        ("Underlier", trade.underlier.toUpperCase),
        ("Last", trade.undLast),
        ("Long Leg", trade.longStrike+" "+callOrPut),
        ("Short Leg", trade.shortStrike+" "+callOrPut),
        ("Expires", trade.expMonthYear),
        ("Strategy", stratWithDescription(trade)._1)
      )
  }
  
  def stratWithDescription(trade: TwoLegTrade): (String, String) = {
    return trade match {
      case trade: BullCall => ("Bull Call", "Buy calls and sell an equal amount of calls with a higher strike")
			case trade: BearCall => ("Bear Call", "Sell calls and buy an equal amount of calls with a higher strike")
			case trade: BullPut => ("Bull Put", "Sell puts and buy an equal amount of puts with a lower strike")
			case trade: BearPut => ("Bear Put", "Buy puts and sell an equal amount of puts with a lower strike")
    }
  }
  
}