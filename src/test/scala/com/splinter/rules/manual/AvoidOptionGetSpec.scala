package com.splinter.rules.manual

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.meta._

class AvoidOptionGetSpec extends AnyFlatSpec with Matchers {

  "AvoidOptionGet" should "detect Option.get usage without parens" in {
    val code = "val x = opt.get"
    val tree = code.parse[Stat].get
    val issues = AvoidOptionGet.check(tree)
    
    issues should have size 1
    issues.head.message should include ("Avoid using Option.get")
  }

  it should "detect Option.get usage with parens" in {
    val code = "val x = opt.get()"
    val tree = code.parse[Stat].get
    val issues = AvoidOptionGet.check(tree)
    
    issues should have size 1
  }

  it should "ignore Map.get usage (with args)" in {
    val code = "val x = map.get(key)"
    val tree = code.parse[Stat].get
    val issues = AvoidOptionGet.check(tree)
    
    issues should be (empty)
  }
}