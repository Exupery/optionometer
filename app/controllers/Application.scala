package controllers

import play.api._
import play.api.mvc._

object Application extends Controller {
  
  def index = Action {
    Ok(views.html.index())
  }
  
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
  
  def disclaimer = Action {
    Ok(views.html.disclaimer())
  }
  
  def faq = Action {
    Ok(views.html.faq())
  }
  
  def privacy = Action {
    Ok(views.html.privacy())
  }
  
  def recommendations = Action {
    Ok(views.html.recommendations())
  }
  
  def tos = Action {
    Ok(views.html.tos())
  }
  
  def removeTrailing(path: String) = Action {
    MovedPermanently("/"+path)
  }
}