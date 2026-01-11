package com.splinter.rules.manual

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.meta._

class AvoidNullSpec extends AnyFlatSpec with Matchers {

  "AvoidNull" should "detect null assignment" in {
    val code = "val x = null"
    val tree = code.parse[Stat].get
    val issues = AvoidNull.check(tree)
    
    issues should have size 1
    issues.head.message should include ("Avoid using 'null'")
  }

  it should "detect null in equality check" in {
    val code = "if (x == null) println(1)"
    val tree = code.parse[Stat].get
    val issues = AvoidNull.check(tree)
    
    issues should have size 1
  }
  
  it should "detect null in pattern matching" in {
    val code = "x match { case null => 1 }"
    val tree = code.parse[Stat].get
    val issues = AvoidNull.check(tree)
    
    issues should have size 1
  }
}