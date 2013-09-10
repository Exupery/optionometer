package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import controllers.Screener.ScreenParams

class BearCallSpec extends Specification {
  
  val trade = {
    running(FakeApplication()) {
    	controllers.Screener.screen(ScreenParams("bearcall", "all"))(0)
    }
  }
  
  "a BearCall" should {
    
    "have a short strike lower than the long strike" in {
      trade.longStrike must be_==(trade.higherStrike)
      trade.shortStrike must be_==(trade.lowerStrike)
      trade.shortStrike must be_<(trade.longStrike)
    }
    
    "have a max profit price equal to the lower strike" in {
      trade.maxProfitPrice must be_==(trade.lowerStrike)
    }
    
    "have a max loss price equal to the higher strike" in {
      trade.maxLossPrice must be_==(trade.higherStrike)
    }
    
    "have a breakeven price equal to the lower strike plus credit received" in {
      trade.breakevenPrice must be_==(trade.higherStrike-(trade.shortBid-trade.longAsk))
    }
    
    "have a max profit equal to credit from short minus debit for long" in {
      trade.maxProfitAmount must be_==(trade.shortBid-trade.longAsk)
    }
    
    "have a max loss equal to difference in strikes minus premium received" in {
      val strikeDiff = trade.higherStrike-trade.lowerStrike
      val credit = trade.shortBid-trade.longAsk
      trade.maxLossAmount must be_==(strikeDiff-credit)
    }
    
  }  

}