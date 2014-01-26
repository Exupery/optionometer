package models

case object BullCalls extends Strategy with Bullish with Calls {
  val name = "Bull Call"
  val description = "Buy calls and sell an equal amount of calls with a higher strike"
  override def toString: String = "bullcalls"
}

case object BearCalls extends Strategy with Bearish with Calls {
  val name = "Bear Call"
  val description = "Sell calls and buy an equal amount of calls with a higher strike"
  override def toString: String = "bearcalls"
}

case object BullPuts extends Strategy with Bullish with Puts {
  val name = "Bull Put"
  val description = "Sell puts and buy an equal amount of puts with a lower strike"
  override def toString: String = "bullputs"
}

case object BearPuts extends Strategy with Bearish with Puts {
  val name = "Bear Put"
  val description = "Buy puts and sell an equal amount of puts with a lower strike"
  override def toString: String = "bearputs"
}

case object AllBullish extends Strategy with Bullish {
  override def toString: String = "bullish"
}

case object AllBearish extends Strategy with Bearish {
  override def toString: String = "bearish"
}

case object LongCallButterflies extends Strategy with Rangebound with Calls {
  val name = "Long Call Butterfly"
  val description = "Buy a call, sell two calls at the next strike, buy a call at the next strike"
  override def toString: String = "longcallbutterflies"
}

case object LongPutButterflies extends Strategy with Rangebound with Puts {
  val name = "Long Put Butterfly"
  val description = "Buy a put, sell two puts at the next strike, buy a put at the next strike"
  override def toString: String = "longputbutterflies"
}

case object AllRangebound extends Strategy with Rangebound {
  override def toString: String = "rangebound"
}

case object All extends Strategy {
  override def toString: String = "all"
}

class Strategy() {
  
  def isBullish: Boolean = this.isInstanceOf[Bullish]
  def isBearish: Boolean = this.isInstanceOf[Bearish]
  def isRangebound: Boolean = this.isInstanceOf[Rangebound]
  
}

object Strategy {
  val BullCalls: Strategy = models.BullCalls
  val BearCalls: Strategy = models.BearCalls
  val BullPuts: Strategy = models.BullPuts
  val BearPuts: Strategy = models.BearPuts
  val AllBullish: Strategy = models.AllBullish
  val AllBearish: Strategy = models.AllBearish
  val LongCallButterflies: Strategy = models.LongCallButterflies
  val LongPutButterflies: Strategy = models.LongPutButterflies
  val AllRangebound: Strategy = models.AllRangebound
  val All: Strategy = models.All
}
