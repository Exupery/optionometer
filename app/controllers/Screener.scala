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
	//TODO perform screen
    val u = "MSFT"
	val qry = "SELECT COUNT(*) FROM options WHERE underlier={underlier}"
	val sql = SQL(qry).on("underlier"->u)
	val foo = runQuery(sql)
//    val foo = DB.withConnection { implicit c =>
//      sql().map { row =>
//      	println(row)	//DELME
//      	row
//      }.toList  
//    }
    println(foo.getClass)	//DELME
    println(foo)	//DELME
    
    println(strat, und, moneyness, minDays, maxDays)	//DELME  
  }
  
  def runQuery(sql: SimpleSql[anorm.Row]): List[Row] = {
    DB.withConnection(implicit c => sql().toList)
  }

}