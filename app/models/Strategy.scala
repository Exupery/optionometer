package models

case object BullCalls extends Strategy {
  override def toString: String = "bullcalls"
}

case object BearCalls extends Strategy {
  override def toString: String = "bearcalls"
}

case object BullPuts extends Strategy {
  override def toString: String = "bullputs"
}

case object BearPuts extends Strategy {
  override def toString: String = "bearputs"
}

case object AllBullish extends Strategy {
  override def toString: String = "bullish"
}

case object AllBearish extends Strategy {
  override def toString: String = "bearish"
}

case object All extends Strategy {
  override def toString: String = "all"
}

class Strategy() {
  
  def isBullish: Boolean = {
    this.equals(BullCalls) || this.equals(BullPuts) || this.equals(AllBullish)
  }
  
  def isBearish: Boolean = {
    this.equals(BearCalls) || this.equals(BearPuts) || this.equals(AllBearish)
  }
  
}

object Strategy {
  val BullCalls: Strategy = models.BullCalls
  val BearCalls: Strategy = models.BearCalls
  val BullPuts: Strategy = models.BullPuts
  val BearPuts: Strategy = models.BearPuts
  val AllBullish: Strategy = models.AllBullish
  val AllBearish: Strategy = models.AllBearish
  val All: Strategy = models.All
}
