package com.splinter.rules.autofix

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.meta._

class PreferFindSpec extends AnyFlatSpec with Matchers {

  "PreferFind" should "detect .filter(...).headOption" in {
    val code = "list.filter(x => x > 5).headOption"
    val tree = code.parse[Stat].get
    val issues = PreferFind.check(tree)
    
    issues should have size 1
    issues.head.message should include ("Use .find() instead")
  }

  it should "provide a correct fix" in {
    val code = "list.filter(x => x > 5).headOption"
    val tree = code.parse[Stat].get
    val issues = PreferFind.check(tree)
    
    issues.head.fix.get.replacement shouldBe "find(x => x > 5)"
  }
}