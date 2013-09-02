package controllers

import anorm._ 
import play.api._
import play.api.Play.current
import play.api.db.DB
import play.api.mvc._
import models._

object Screener extends Controller {
  
  def screenerNoParams = Action {
    Ok(views.html.screener())
  }
  
  def screenerWithParams(strat: String, und: String, moneyness: Option[String], minDays: Option[Int], maxDays: Option[Int]) = Action {
    val trades = screen(strat, und, moneyness, minDays, maxDays)
    Ok(views.html.trades(trades))
  }
  
  def screen(strat: String, und: String, moneyness: Option[String]=None, minDays: Option[Int]=None, maxDays: Option[Int]=None): List[TwoLegTrade] = {
	println(strat, und, moneyness, minDays, maxDays)	//DELME  
    val u = "MSFT"	//TODO get from params
    val cp = "C"	//TODO get from params
	val qry = """
	  	SELECT * FROM twolegs WHERE FROM_UNIXTIME(expires)>NOW() AND
		underlier={underlier} AND
		callOrPut={callOrPut} AND
		longStrike<shortStrike LIMIT 5;
	  """	//TODO: revmove limit
	val sql = SQL(qry).on("underlier"->u, "callOrPut"->cp)
    val trades: List[TwoLegTrade] = runQuery(sql).map { row =>
      new BullCall(row)	
	}
	println(trades)			//DELME
	println(trades.size)	//DELME
//	println(trades(0).underlier,trades(0).undLast,trades(0).expires,trades(0).longSym,trades(0).shortSym)	//DELME
//	println(trades(0).longStrike,trades(0).longBid,trades(0).longAsk,trades(0).shortStrike,trades(0).shortBid,trades(0).shortAsk)	//DELME
//	println(trades(0).cost,trades(0).maxProfitAmount,trades(0).amountToMaxProfit,trades(0).percentPerDayToMaxProfit)	//DELME
//	println(trades(0).amountToMaxLoss,trades(0).percentPerDayToMaxLoss)	//DELME
	return trades
  }
  
  def runQuery(sql: SimpleSql[Row]): List[Row] = {
    DB.withConnection(implicit c => sql().toList)
  }

}