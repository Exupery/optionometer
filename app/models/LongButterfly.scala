package models

import anorm._

abstract class LongButterfly(twoLegA: TwoLegTrade, twoLegB: TwoLegTrade) 
	extends FourLegTrade(twoLegA.underlier, twoLegA.undLast, twoLegA.expires) {

}

abstract class LongCallButterfly(bullCall: BullCall, bearCall: BearCall) extends LongButterfly(bullCall, bearCall) with Calls {
  
  //TODO

}