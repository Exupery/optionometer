package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import controllers.Screener.ScreenParams

@RunWith(classOf[JUnitRunner])
class TradeSpec extends Specification {
  
  val itmTrade = {
    running(FakeApplication()) {
    	controllers.Screener.screen(ScreenParams(Strategy.BullCalls, Seq("all"), Some("itm"))).head
    }
  }
  
  val otmTrade = {
    running(FakeApplication()) {
    	controllers.Screener.screen(ScreenParams(Strategy.BullCalls, Seq("all"), Some("otm"))).head
    }
  }

  "Trade" should {
  
    "calculate the number of days until expiration" in {
      val expireUnix = itmTrade.expires
      val nowUnix = System.currentTimeMillis / 1000
      val secondsInDay = 60 * 60 * 24
      val secondsToExpire = expireUnix - nowUnix
      itmTrade.daysToExpire must be_==(secondsToExpire / secondsInDay)
    }
    
    "determine if legs are calls or puts" in {
    	running(FakeApplication()) {
    	  val callTrade = controllers.Screener.screen(ScreenParams(Strategy.BullCalls)).head
    	  val putTrade = controllers.Screener.screen(ScreenParams(Strategy.BullPuts)).head
    	  callTrade.callOrPut must equalTo("C")
    	  putTrade.callOrPut must equalTo("P")
    	}
    }
    
    "calculate the max profit percent" in {
      itmTrade.profitPercent must equalTo(itmTrade.twoDigit(itmTrade.maxProfitAmount / itmTrade.maxLossAmount * 100))
      otmTrade.profitPercent must equalTo(otmTrade.twoDigit(otmTrade.maxProfitAmount / otmTrade.maxLossAmount * 100))
    }
    
    "calculate the max profit percent per day" in {
      val itmProfit = itmTrade.maxProfitAmount / itmTrade.maxLossAmount * 100
      itmTrade.profitPercentPerDay must equalTo(itmTrade.twoDigit(itmProfit / itmTrade.daysToExpire))
      val otmProfit = otmTrade.maxProfitAmount / otmTrade.maxLossAmount * 100
      otmTrade.profitPercentPerDay must equalTo(otmTrade.twoDigit(otmProfit / otmTrade.daysToExpire))
    }
    
    "calculate percent per day to max profit" in {
      itmTrade.percentPerDayToMaxProfit must equalTo(itmTrade.twoDigit(itmTrade.percentToMaxProfit / itmTrade.daysToExpire))
      otmTrade.percentPerDayToMaxProfit must equalTo(otmTrade.twoDigit(otmTrade.percentToMaxProfit / otmTrade.daysToExpire))
    }
    
    "calculate percent per day to max loss" in {
      itmTrade.percentPerDayToMaxLoss must equalTo(itmTrade.twoDigit(itmTrade.percentToMaxLoss / itmTrade.daysToExpire))
    	otmTrade.percentPerDayToMaxLoss must equalTo(otmTrade.twoDigit(otmTrade.percentToMaxLoss / otmTrade.daysToExpire))
    }
    
    "calculate percent per day to breakeven" in {
    	itmTrade.percentPerDayToBreakeven must equalTo(itmTrade.twoDigit(itmTrade.percentToBreakeven / itmTrade.daysToExpire))
    	otmTrade.percentPerDayToBreakeven must equalTo(otmTrade.twoDigit(otmTrade.percentToBreakeven / otmTrade.daysToExpire))
    }
    
  }
  
}