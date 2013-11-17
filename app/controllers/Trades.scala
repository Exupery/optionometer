package controllers

import anorm._ 
import play.api._
import play.api.Play.current
import play.api.db.DB
import play.api.mvc._
import models._

object Trades extends Controller {
  
  def tradesNoParams(und: String) = Action {
    Redirect(routes.Screener.screenerWithParams("all", und, None, None, None, None, None, None))
  }
  
  def trades(
      und: String,
      year: String,
      month: String,
      leg1: String,
      leg2: String) = Action {
    //MSFT/2014/01/L3400P/S3500P
    println(und, year, month, leg1, leg2)	//DELME
    Ok
  }

}