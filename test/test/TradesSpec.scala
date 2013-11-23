package test

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import com.github.nscala_time.time.Imports._
import controllers.Trades.TradeParams

class TradesSpec extends Specification {
  
  "Trades" should {
    
    "calculate min & max days" in {
      val now = DateTime.now
      controllers.Trades.minMaxDays(now.getYear, now.getMonthOfYear-1) must equalTo((None, None))
      controllers.Trades.minMaxDays(now.getYear-1, now.getMonthOfYear) must equalTo((None, None))
      controllers.Trades.minMaxDays(now.getYear-1, now.getMonthOfYear-1) must equalTo((None, None))
      controllers.Trades.minMaxDays(now.getYear, now.getMonthOfYear) must not be equalTo((None, None))
    }
    
    "return a two leg trade" in {
      running(FakeApplication()) {
	      val now = DateTime.now
	      //TODO get und & legs from DB
	      val params = new TradeParams("MSFT", now.getYear.toString, now.getMonthOfYear.toString, "L34.00P-S35.00P")
	      controllers.Trades.twoLegTrade(params) must beSome
      }
    }
    
  }

}