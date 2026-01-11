package com.splinter.rules.manual

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.meta._

class AvoidCatchingThrowableSpec extends AnyFlatSpec with Matchers {

  "AvoidCatchingThrowable" should "detect catching Throwable with type" in {
    val code = 
      """
      try {
        risky()
      } catch {
        case t: Throwable => log(t)
      }
      """
    val tree = code.parse[Stat].get
    val issues = AvoidCatchingThrowable.check(tree)
    
    issues should have size 1
    issues.head.message should include ("Do not catch 'Throwable'")
  }

  it should "allow catching Exception" in {
    val code = 
      """
      try {
        risky()
      } catch {
        case e: Exception => log(e)
      }
      """
    val tree = code.parse[Stat].get
    val issues = AvoidCatchingThrowable.check(tree)
    
    issues should be (empty)
  }
}
