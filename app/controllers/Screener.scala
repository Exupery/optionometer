package controllers

import anorm._ 
import play.api._
import play.api.Play.current
import play.api.db.DB
import play.api.mvc._

object Screener extends Controller {
  
  def screenerNoParams = Action {
    screen("all", "all")
    Ok(views.html.screener())
  }
  
  def screenerWithParams(strat: String, und: String, moneyness: Option[String], minDays: Option[Int], maxDays: Option[Int]) = Action {
    screen(strat, und, moneyness, minDays, maxDays)
    Ok(views.html.screener())
  }
  
  def screen(strat: String, und: String, moneyness: Option[String]=None, minDays: Option[Int]=None, maxDays: Option[Int]=None) {
	println(strat, und, moneyness, minDays, maxDays)	//DELME  
    //TODO perform screen
    val u = "MSFT"
	val qry = """
	  	SELECT l.underlier, stocks.last_trade, l.exp_unixtime, l.symbol AS longSym, l.bid AS longBid, l.ask AS longAsk, l.strike AS longStrike,
	  	s.symbol AS shortSym, s.bid AS shortBid, s.ask AS shortAsk, s.strike AS shortStrike
	  	FROM options AS l JOIN options AS s ON l.underlier=s.underlier AND 
		l.exp_unixtime=s.exp_unixtime AND l.call_or_put=s.call_or_put 
	  	JOIN stocks ON l.underlier=stocks.symbol 
	  	WHERE FROM_UNIXTIME(l.exp_unixtime)>NOW() AND
		l.underlier={underlier} AND
		l.call_or_put={callOrPut} AND
		l.strike<s.strike LIMIT 1;
	  """	//TODO: revmove limit
	val sql = SQL(qry).on("underlier"->u, "callOrPut"->"C")
	val foo = runQuery(sql)
    println(foo)	//DELME
    
    
  }
  
  def runQuery(sql: SimpleSql[anorm.Row]): List[Row] = {
    DB.withConnection(implicit c => sql().toList)
  }

}