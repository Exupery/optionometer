package controllers

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import java.text.SimpleDateFormat
import java.util.Date
import views.helpers.TradesHelper
import controllers.Screener.ScreenParams
import models.Strategy

@RunWith(classOf[JUnitRunner])
class TradesHelperSpec extends Specification {
  
  val trade = {
    running(FakeApplication()) {
    	controllers.Screener.screen(ScreenParams(Strategy.BullCalls)).toList(0)
    }
  }
  
  "TradesHelperSpec" should {
    
    "determine path to detail view" in {
      val df = new SimpleDateFormat("yyyy/MM")
      val path = trade.underlier + "/" + df.format(new Date(trade.expires * 1000)) + "/" + 
      "L" + trade.longStrike + trade.callOrPut + "-S" + trade.shortStrike + trade.callOrPut
      TradesHelper.detailPath(trade) must be_==(path)
    }
    
    "get trade details" in {
      val details = TradesHelper.details(trade)
      details must beAnInstanceOf[List[(String, Any)]]
      details.nonEmpty must be_==(true)
    }
    
    "get strategy descriptions" in {
      running(FakeApplication()) {
      	val bullcall = controllers.Screener.screen(ScreenParams(Strategy.BullCalls)).toList(0)
      	val bearcall = controllers.Screener.screen(ScreenParams(Strategy.BearCalls)).toList(0)
      	val bullput = controllers.Screener.screen(ScreenParams(Strategy.BullPuts)).toList(0)
      	val bearput = controllers.Screener.screen(ScreenParams(Strategy.BearPuts)).toList(0)
      	TradesHelper.stratWithDescription(bullcall)._1 must beEqualTo("Bull Call")
      	TradesHelper.stratWithDescription(bullcall)._2 must contain("calls")
      	TradesHelper.stratWithDescription(bearcall)._1 must beEqualTo("Bear Call")
      	TradesHelper.stratWithDescription(bearcall)._2 must contain("calls")
      	TradesHelper.stratWithDescription(bullput)._1 must beEqualTo("Bull Put")
      	TradesHelper.stratWithDescription(bullput)._2 must contain("puts")
      	TradesHelper.stratWithDescription(bearput)._1 must beEqualTo("Bear Put")
      	TradesHelper.stratWithDescription(bearput)._2 must contain("puts")
      }
    }
    
    "get profit metrics" in {
      val metrics = TradesHelper.profitMetrics(trade)
      metrics must beAnInstanceOf[List[(String, Any)]]
      metrics.nonEmpty must be_==(true)
    }
    
    "get loss metrics" in {
      val metrics = TradesHelper.lossMetrics(trade)
      metrics must beAnInstanceOf[List[(String, Any)]]
      metrics.nonEmpty must be_==(true)
    }
    
    "determine if trade is presently profitable" in {
      running(FakeApplication()) {
        val bullish = controllers.Screener.screen(ScreenParams(Strategy.BullCalls)).toList(0)
        val bearish = controllers.Screener.screen(ScreenParams(Strategy.BearCalls)).toList(0)
      	TradesHelper.currentlyProfitable(bullish) must be_==(bullish.undLast >= bullish.breakevenPrice)
      	TradesHelper.currentlyProfitable(bearish) must be_==(bearish.undLast <= bearish.breakevenPrice)
      }
    }
    
  }
  
}