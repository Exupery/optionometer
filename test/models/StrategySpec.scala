package models

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class StrategySpec extends Specification {

  "Strategy" should {
    
    "determine strategy is bullish" in {
      Strategy.AllBullish.isBullish must beTrue
      Strategy.AllBullish.isBearish must beFalse
      Strategy.BullCalls.isBullish must beTrue
      Strategy.BullPuts.isBullish must beTrue
    }
    
    "determine strategy is bearish" in {
      Strategy.AllBearish.isBearish must beTrue
      Strategy.AllBearish.isBullish must beFalse
      Strategy.BearCalls.isBearish must beTrue
      Strategy.BearPuts.isBearish must beTrue
    }
    
    "determine strategy is rangebound" in {
      Strategy.LongCallButterflies.isBearish must beFalse
      Strategy.LongCallButterflies.isBullish must beFalse
      Strategy.LongCallButterflies.isRangebound must beTrue
      Strategy.LongPutButterflies.isRangebound must beTrue
    }
    
  }
}