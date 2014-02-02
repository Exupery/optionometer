package views.helpers

import models._
import models.Strategy
import java.text.SimpleDateFormat
import java.util.Date

object TradesHelper {
  
  def detailPath(trade: Trade): String = {
    return {
      val df = new SimpleDateFormat("yyyy/MM")
      trade.underlier + "/" + df.format(new Date(trade.expires * 1000)) + "/" + (trade.legsFull.replaceAll("\\s", "-"))
    }
  }
  
  def details(trade: Trade): List[(String, Any)] = {
    val callOrPut = if (trade.callOrPut.equalsIgnoreCase("C")) "Call" else "Put"
    return List(
      ("Underlier", trade.underlier.toUpperCase),
      ("Last", trade.undLast),
      ("Expires", trade.expMonthYear),
      ("Strategy", stratWithDescription(trade)._1)
    ) ++ (trade match {
      case t: TwoLegTrade => List(
        ("Long Leg", t.longStrike+" "+callOrPut), 
        ("Short Leg", t.shortStrike+" "+callOrPut)
      )
      case t: FourLegTrade => List(
        ("Long Legs", t.longStrikes.map(_+" "+callOrPut).mkString(" ")), 
        ("Short Legs", t.shortStrikes.map(_+" "+callOrPut).mkString(" "))
      )
    })
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
  
  def profitMetrics(trade: Trade): List[(String, Any)] = {
    return List(
      ("Max profit amount", "$"+trade.maxProfitAmount*100),
      ("Max profit percent", trade.profitPercent+"%"),
      ("Profit percent per day", trade.profitPercentPerDay+"%")
    ) ++ (trade match {
      case t: TwoLegTrade => profitMetrics(t)
      case t: FourLegTrade => profitMetrics(t)
    })
  }

  private def profitMetrics(trade: TwoLegTrade): List[(String, Any)] = {
    val aboveBelow = if (trade.undLast > trade.maxProfitPrice) "above" else "below" 
    return List(
      ("Max profit price", trade.maxProfitPrice),
      ("Distance "+aboveBelow+" max profit", Math.abs(trade.percentToMaxProfit.toDouble)+"%")
    )
  }
  
  private def profitMetrics(trade: FourLegTrade): List[(String, Any)] = {
    val aboveBelow = if (trade.undLast > trade.maxProfitPrice) "above" else "below" 
    return List(
      ("Max profit point(s)", trade.lowerMaxProfitPrice + 
        (if (trade.lowerMaxProfitPrice!=trade.higherMaxProfitPrice) " - " + trade.higherMaxProfitPrice else "")),
      ("Distance "+aboveBelow+" nearest max profit", Math.abs(trade.percentToMaxProfit.toDouble)+"%")
    )
  }
  
  def lossMetrics(trade: Trade): List[(String, Any)] = {
    return List(
      ("Max loss amount", "$"+trade.maxLossAmount*100)
    ) ++ (trade match {
      case t: TwoLegTrade => lossMetrics(t)
      case t: FourLegTrade => lossMetrics(t)
    })
  }
  
  def lossMetrics(trade: TwoLegTrade): List[(String, Any)] = {
    val aboveBelowMaxLoss = if (trade.undLast > trade.maxLossPrice) "above" else "below"
    val aboveBelowBreakeven = if (trade.undLast > trade.breakevenPrice) "above" else "below" 
    return List(
      ("Max loss price", trade.maxLossPrice),
      ("Distance "+aboveBelowMaxLoss+" max loss", Math.abs(trade.percentToMaxLoss.toDouble)+"%"),
      ("Breakeven price", trade.breakevenPrice),
      ("Distance "+aboveBelowBreakeven+" breakeven", Math.abs(trade.percentToBreakeven.toDouble)+"%")
      
    )
  }
  
  def lossMetrics(trade: FourLegTrade): List[(String, Any)]	= {
    val aboveBelowMaxLoss = if (trade.undLast > trade.maxLossPrice) "above" else "below"
    val aboveBelowBreakeven = if (trade.undLast > trade.breakevenPrice) "above" else "below" 
    return List(
      ("Max loss point(s)", trade.lowerMaxLossPrice+", "+trade.higherMaxLossPrice),
      ("Distance "+aboveBelowMaxLoss+" nearest max loss", Math.abs(trade.percentToMaxLoss.toDouble)+"%"),
      ("Nearest breakeven price", trade.breakevenPrice),
      ("Distance "+aboveBelowBreakeven+" nearest breakeven", Math.abs(trade.percentToBreakeven.toDouble)+"%")
      
    )
  }  
  
  def panelClass(trade: Trade): String = {
    return if (trade.isItm) "panel-success" else if (trade.isProfitable) "panel-warning" else "panel-danger"
  }
  
}