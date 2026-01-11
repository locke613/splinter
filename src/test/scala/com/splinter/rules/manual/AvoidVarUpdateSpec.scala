package com.splinter.rules.manual

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.meta._

class AvoidVarUpdateSpec extends AnyFlatSpec with Matchers {

  "AvoidVarUpdate" should "detect var assignment inside a for loop" in {
    val code = 
      """
      {
        var x = 0
        for (i <- 1 to 10) {
          x = i // Bad
        }
      }
      """
    val tree = code.parse[Stat].get
    val issues = AvoidVarUpdate.check(tree)
    
    issues should have size 1
    issues.head.message should include ("Avoid updating vars inside loops")
  }

  it should "detect var update (+=) inside an if condition" in {
    val code = 
      """
      if (true) {
        x += 1 // Bad
      }
      """
    val tree = code.parse[Stat].get
    val issues = AvoidVarUpdate.check(tree)
    
    issues should have size 1
    issues.head.message should include ("Avoid updating vars inside loops or conditions")
  }

  it should "ignore var assignment at top level" in {
    val code = 
      """
      x = 1 // OK (assuming x is defined elsewhere)
      """
    val tree = code.parse[Stat].get
    val issues = AvoidVarUpdate.check(tree)
    
    issues should be (empty)
  }
}