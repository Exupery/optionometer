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

  def profitMetrics(trade: TwoLegTrade): List[(String, Any)] = {
    val aboveBelow = if (trade.undLast > trade.maxProfitPrice) "above" else "below" 
    return List(
        ("Max profit price", trade.maxProfitPrice),
        ("Distance "+aboveBelow+" max profit", Math.abs(trade.percentToMaxProfit.toDouble)+"%"),
        ("Max profit amount", "$"+trade.maxProfitAmount*100),
        ("Max profit percent", trade.profitPercent+"%"),
        ("Profit percent per day", trade.profitPercentPerDay+"%")
      )
  }
  
  def lossMetrics(trade: TwoLegTrade): List[(String, Any)] = {
    val aboveBelowMaxLoss = if (trade.undLast > trade.maxLossPrice) "above" else "below"
    val aboveBelowBreakeven = if (trade.undLast > trade.breakevenPrice) "above" else "below" 
    return List(
        ("Max loss price", trade.maxLossPrice),
        ("Distance "+aboveBelowMaxLoss+" max loss", Math.abs(trade.percentToMaxLoss.toDouble)+"%"),
        ("Max loss amount", "$"+trade.maxLossAmount*100),
        ("Breakeven price", trade.breakevenPrice),
        ("Distance "+aboveBelowBreakeven+" breakeven", Math.abs(trade.percentToBreakeven.toDouble)+"%")
        
      )
  }
  
  def panelClass(trade: TwoLegTrade): String = {
    return if (trade.undLast >= trade.maxLossPrice && trade.undLast <= trade.maxProfitPrice) {
      "panel-warning"
    } else {
      if (trade.isItm) "panel-success" else "panel-danger"
    }
  }
  
}