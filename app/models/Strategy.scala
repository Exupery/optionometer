package models

case object BullCalls extends Strategy with Bullish {
  override def toString: String = "bullcalls"
}

case object BearCalls extends Strategy with Bearish{
  override def toString: String = "bearcalls"
}

case object BullPuts extends Strategy with Bullish {
  override def toString: String = "bullputs"
}

case object BearPuts extends Strategy with Bearish {
  override def toString: String = "bearputs"
}

case object AllBullish extends Strategy with Bullish {
  override def toString: String = "bullish"
}

case object AllBearish extends Strategy with Bearish {
  override def toString: String = "bearish"
}

case object LongCallButterflies extends Strategy with Rangebound {
  override def toString: String = "longcallbutterflies"
}

case object LongPutButterflies extends Strategy with Rangebound {
  override def toString: String = "longputbutterflies"
}

case object AllRangebound extends Strategy with Rangebound {
  override def toString: String = "rangebound"
}

case object All extends Strategy {
  override def toString: String = "all"
}

class Strategy() {
  
  val bullish = Set(Strategy.BullCalls, Strategy.BullCalls, Strategy.AllBullish)
  
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
