package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import controllers.Screener.ScreenParams

@RunWith(classOf[JUnitRunner])
class TradeSpec extends Specification {
  
  val trade = {
    running(FakeApplication()) {
    	controllers.Screener.screen(ScreenParams(Strategy.BullCalls)).head
    }
  }

  "Trade" should {
  
    "calculate the number of days until expiration" in {
      val expireUnix = trade.expires
      val nowUnix = System.currentTimeMillis / 1000
      val secondsInDay = 60 * 60 * 24
      val secondsToExpire = expireUnix - nowUnix
      trade.daysToExpire must be_==(secondsToExpire / secondsInDay)
    }
    
    "determine if legs are calls or puts" in {
    	running(FakeApplication()) {
    	  val callTrade = controllers.Screener.screen(ScreenParams(Strategy.BullCalls)).head
    	  val putTrade = controllers.Screener.screen(ScreenParams(Strategy.BullPuts)).head
    	  callTrade.callOrPut must equalTo("C")
    	  putTrade.callOrPut must equalTo("P")
    	}
    }
    
  }
  
}