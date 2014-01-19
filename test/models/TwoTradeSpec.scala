package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import controllers.Screener.ScreenParams
import scala.math.BigDecimal.int2bigDecimal

@RunWith(classOf[JUnitRunner])
class TwoTradeSpec extends Specification {
  
  def trade = controllers.Screener.screen(ScreenParams(Strategy.BullCalls)).head
  def twoDigit(bigDec: BigDecimal): BigDecimal = bigDec.setScale(2, BigDecimal.RoundingMode.HALF_UP)
  
  "a TwoLegTrade" should {
    
    "calculate the max profit percent" in {
      running(FakeApplication()) {
      	trade.profitPercent must equalTo(twoDigit((trade.maxProfitAmount / trade.maxLossAmount) * 100))
      }
    }
    
  }

}