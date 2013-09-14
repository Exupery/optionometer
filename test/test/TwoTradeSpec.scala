package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._
import controllers.Screener.ScreenParams

class TwoTradeSpec extends Specification {
  
  val trade = {
    running(FakeApplication()) {
    	controllers.Screener.screen(ScreenParams("bullcalls", "all"))(0)
    }
  }
  
  def twoDigit(bigDec: BigDecimal): BigDecimal = bigDec.setScale(2, BigDecimal.RoundingMode.HALF_UP)
  
  "a TwoLegTrade" should {
    
    "calculate the number of days until expiration" in {
      val expireUnix = trade.expires
      val nowUnix = System.currentTimeMillis / 1000
      val secondsInDay = 60 * 60 * 24
      val secondsToExpire = expireUnix - nowUnix
      trade.daysToExpire must be_==(secondsToExpire / secondsInDay)
    }
    
    "calculate the max profit percent" in {
      trade.profitPercent must be_==(twoDigit((trade.maxProfitAmount / trade.maxLossAmount) * 100))
    }
    
  }

}