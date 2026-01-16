package com.splinter.rules.manual

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.meta._

class AvoidDeepNestingSpec extends AnyFlatSpec with Matchers {

  "AvoidDeepNesting" should "not report issues for shallow nesting" in {
    val code =
      """
        |if (c1) {
        |  if (c2) {
        |    println("ok")
        |  }
        |}
        |""".stripMargin
    val tree = code.parse[Stat].get
    val issues = AvoidDeepNesting.check(tree)
    issues should be(empty)
  }

  it should "report issues for nesting deeper than 3 levels" in {
    val code =
      """
        |if (c1) {
        |  if (c2) {
        |    if (c3) {
        |      if (c4) {
        |        println("too deep")
        |      }
        |    }
        |  }
        |}
        |""".stripMargin
    val tree = code.parse[Stat].get
    val issues = AvoidDeepNesting.check(tree)
    issues should have size 1
    issues.head.message should include("deeper than 3 layers")
  }

  it should "not count else-if chains as nesting" in {
    val code =
      """
        |if (c1) {
        |  println(1)
        |} else if (c2) {
        |  println(2)
        |} else if (c3) {
        |  println(3)
        |} else if (c4) {
        |  println(4)
        |}
        |""".stripMargin
    val tree = code.parse[Stat].get
    val issues = AvoidDeepNesting.check(tree)
    issues should be(empty)
  }

  it should "count nesting inside else blocks when not an else-if chain" in {
    val code =
      """
        |if (c1) {
        |  println(1)
        |} else {
        |  println(2) // breaks else-if chain detection
        |  if (c2) {
        |    println(3)
        |  } else {
        |    if (c5) {
        |      println(5)
        |    } else {
        |      if (c6) {
        |        println("too deep")
        |      }
        |    }
        |  }
        |}
        |""".stripMargin
    val tree = code.parse[Stat].get
    val issues = AvoidDeepNesting.check(tree)
    issues should have size 1
  }
}