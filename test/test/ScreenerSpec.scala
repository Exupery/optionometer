package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import controllers.Screener.ScreenParams
import models.Strategy._

class ScreenerSpec extends Specification {
  
  "Screener" should {
    
	  "create strike clause" in {
			controllers.Screener.strikeClause(AllBullish) must equalTo("longStrike < shortStrike")
			controllers.Screener.strikeClause(BullCalls) must equalTo("longStrike < shortStrike")
			controllers.Screener.strikeClause(BullPuts) must equalTo("longStrike < shortStrike")
			controllers.Screener.strikeClause(AllBearish) must equalTo("longStrike > shortStrike")
			controllers.Screener.strikeClause(BearCalls) must equalTo("longStrike > shortStrike")
			controllers.Screener.strikeClause(BearPuts) must equalTo("longStrike > shortStrike")
			controllers.Screener.strikeClause(All) must equalTo("longStrike != shortStrike")
	  }
      
      "create money clause" in {
        val anyClause = "shortStrike BETWEEN (undLast*0.96) AND (undLast*1.04)"
        val ntmClause = "shortStrike BETWEEN (undLast*0.99) AND (undLast*1.01)"
        val bullStrats = List(AllBullish, BullCalls, BullPuts)
        val bearStrats = List(AllBearish, BearCalls, BearPuts)
        
        bullStrats.foreach { strat =>
          controllers.Screener.moneyClause(strat, "itm") must equalTo("undLast > shortStrike")
	    	  controllers.Screener.moneyClause(strat, "otm") must equalTo("undLast < shortStrike")
	    	  controllers.Screener.moneyClause(strat, "ntm") must equalTo(ntmClause)
	    	  controllers.Screener.moneyClause(strat, "any") must equalTo(anyClause)
        }
        
        bearStrats.foreach { strat =>
          controllers.Screener.moneyClause(strat, "itm") must equalTo("undLast < shortStrike")
	    	  controllers.Screener.moneyClause(strat, "otm") must equalTo("undLast > shortStrike")
	    	  controllers.Screener.moneyClause(strat, "ntm") must equalTo(ntmClause)
	    	  controllers.Screener.moneyClause(strat, "any") must equalTo(anyClause)
        }
          
      }
      
      "create expiration clause" in {
        controllers.Screener.daysClause(Some(0), Some(365)) must startWith("expires BETWEEN")
      }
      
      "return a list of trades" in {
        running(FakeApplication()) {
          val underliers = List(Seq("all"), Seq("MSFT"), Seq("MSFT", "INTC"))
          underliers.foreach { unds =>
	          val params: ScreenParams = ScreenParams(BullCalls, unds, None, None, None)
	          val result = controllers.Screener.screen(params)
	          result.size must beGreaterThan(0)
          }
        }
      }
  }
}