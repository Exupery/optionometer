package controllers

import play.api._
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
    
    println(strat, und, moneyness, minDays, maxDays)	//DELME  
  }  

}