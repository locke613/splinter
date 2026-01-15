package com.splinter.rules.autofix

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.meta._

class AvoidCollectSpec extends AnyFlatSpec with Matchers {

  "AvoidCollect" should "detect .collect() usage" in {
    val code = "val data = df.collect()"
    val tree = code.parse[Stat].get
    val issues = AvoidCollect.check(tree)

    issues should have size 1
    issues.head.message should include("Avoid using .collect()")
  }

  it should "suggest toLocalIterator as fix" in {
    val code = "df.collect()"
    val tree = code.parse[Stat].get
    val issues = AvoidCollect.check(tree)

    issues.head.fix should be(defined)
    issues.head.fix.get.replacement shouldBe "toLocalIterator"
  }
}
