package com.splinter.rules.manual

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.meta._

class AvoidHeadSpec extends AnyFlatSpec with Matchers {

  "AvoidHead" should "detect .head usage" in {
    val code = "val x = list.head"
    val tree = code.parse[Stat].get
    val issues = AvoidHead.check(tree)
    
    issues should have size 1
    issues.head.message should include ("Avoid using .head")
  }

  // Note: .head usually takes no arguments, so we don't strictly need to test argument exclusion like Option.get, but the rule handles it safely.
}