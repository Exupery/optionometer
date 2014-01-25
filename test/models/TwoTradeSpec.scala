package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import controllers.Screener.ScreenParams

@RunWith(classOf[JUnitRunner])
class TwoTradeSpec extends Specification {
  
  val trade = {
    running(FakeApplication()) {
    	controllers.Screener.screen(ScreenParams(Strategy.BullCalls)).head
    }
  }
  
  "a TwoLegTrade" should {
    
    "calculate the max profit percent" in {
    	trade.profitPercent must equalTo(trade.twoDigit((trade.maxProfitAmount / trade.maxLossAmount) * 100))
    }
    
  }

}