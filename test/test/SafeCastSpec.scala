package test

import org.specs2.mutable._
import controllers.SafeCast

class SafeCastSpec extends Specification with SafeCast {
  
  "SafeCast" should {
    
    "safely cast a String to Int" in {
      safeInt("") must equalTo(0)
      safeInt("0") must equalTo(0)
      safeInt("NAN") must equalTo(0)
      safeInt("1") must equalTo(1)
    }
    
    "safely cast a String to BigDecimal" in {
      safeBigDecimal("") must equalTo(0)
      safeBigDecimal("0") must equalTo(0)
      safeBigDecimal("NAN") must equalTo(0)
      safeBigDecimal("1") must equalTo(1)
    }
    
  }

}