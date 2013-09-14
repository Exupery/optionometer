package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import controllers.Screener.ScreenParams
import models.Strategy

class BullCallSpec extends Specification {
  
  val trade = {
    running(FakeApplication()) {
    	controllers.Screener.screen(ScreenParams(Strategy.BullCalls, "all"))(0)
    }
  }
  
  "a BullCall" should {
    
    "have a long strike lower than the short strike" in {
      trade.longStrike must be_==(trade.lowerStrike)
      trade.shortStrike must be_==(trade.higherStrike)
      trade.longStrike must be_<(trade.shortStrike)
    }
    
    "have a max profit price equal to the higher strike" in {
      trade.maxProfitPrice must be_==(trade.higherStrike)
    }
    
    "have a max loss price equal to the lower strike" in {
      trade.maxLossPrice must be_==(trade.lowerStrike)
    }
    
    "have a breakeven price equal to the lower strike plus debit paid" in {
      trade.breakevenPrice must be_==(trade.lowerStrike+(trade.longAsk-trade.shortBid))
    }
    
    "have a max profit equal to difference in strike minus cost of trade" in {
      val strikeDiff = trade.higherStrike-trade.lowerStrike
      val cost = trade.longAsk-trade.shortBid
      trade.maxProfitAmount must be_==(strikeDiff-cost)
    }
    
    "have a max loss equal to the debit paid for long minus the credit from short" in {
      trade.maxLossAmount must be_==(trade.longAsk-trade.shortBid)
    }
    
  }
  
}