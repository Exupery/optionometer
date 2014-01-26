package views.helpers

import models._
import models.Strategy
import java.text.SimpleDateFormat
import java.util.Date

object TradesHelper {
  
  def detailPath(trade: Trade): String = {
    return {
      val df = new SimpleDateFormat("yyyy/MM")
      trade.underlier + "/" + df.format(new Date(trade.expires * 1000)) + "/" + (trade.legs.replaceAll("\\s", "-"))
    }
  }
  
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
  
  def stratWithDescription(trade: Trade): (String, String) = {
    return trade match {
      case strat: BullCall => (BullCalls.name, BullCalls.description)
			case strat: BearCall => (BearCalls.name, BearCalls.description)
			case strat: BullPut => (BullPuts.name, BullPuts.description)
			case strat: BearPut => (BearPuts.name, BearPuts.description)
			case strat: LongCallButterfly => (LongCallButterflies.name, LongCallButterflies.description)
			case strat: LongPutButterfly => (LongPutButterflies.name, LongPutButterflies.description)
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
  
  def currentlyProfitable(trade: TwoLegTrade): Boolean = {
    return if (trade.isInstanceOf[Bullish]) {
      trade.undLast >= trade.breakevenPrice
    } else {
      trade.undLast <= trade.breakevenPrice
    }
  }
  
  def panelClass(trade: TwoLegTrade): String = {
    return if (trade.undLast >= trade.maxLossPrice && trade.undLast <= trade.maxProfitPrice) {
      "panel-warning"
    } else {
      if (trade.isItm) "panel-success" else "panel-danger"
    }
  }
  
}