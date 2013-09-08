package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import controllers.Screener.ScreenParams

class ScreenerSpec extends Specification {
  
  "Screener" should {
    
	  "create strike clause" in {
			controllers.Screener.strikeClause("bullish") must equalTo("longStrike < shortStrike")
			controllers.Screener.strikeClause("bullcalls") must equalTo("longStrike < shortStrike")
			controllers.Screener.strikeClause("bullputs") must equalTo("longStrike < shortStrike")
			controllers.Screener.strikeClause("bearish") must equalTo("longStrike > shortStrike")
			controllers.Screener.strikeClause("bearcalls") must equalTo("longStrike > shortStrike")
			controllers.Screener.strikeClause("bearputs") must equalTo("longStrike > shortStrike")
			controllers.Screener.strikeClause("all") must equalTo("longStrike != shortStrike")
	  }
      
      "create money clause" in {
        val anyClause = "shortStrike BETWEEN (undLast*0.75) AND (undLast*1.25)"
        val ntmClause = "shortStrike BETWEEN (undLast*0.975) AND (undLast*1.025)"
        val bullStrats = List("bullish", "bullcalls", "bullputs")
        val bearStrats = List("bearish", "bearcalls", "bearputs")
        
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
          val params: ScreenParams = ScreenParams("any", "all", None, None, None)
          val result = controllers.Screener.screen(params)
          result.size must beGreaterThan(0)
        }
      }
  }
}