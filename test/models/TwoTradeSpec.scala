package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import controllers.Screener.ScreenParams

@RunWith(classOf[JUnitRunner])
class TwoTradeSpec extends Specification {
  
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
  
  "a TwoLegTrade" should {
    
    "determine if trade is in or out of the money" in {
    	itmTrade.isItm must beTrue
    	otmTrade.isItm must beFalse
    }
    
  }

}