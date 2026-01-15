package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/** Rule to detect usage of 'null'. Null values are error-prone and invisible to
  * the type system. Use Option[T] instead.
  * https://github.com/alexandru/scala-best-practices/blob/master/sections/2-language-rules.md#29-must-not-use-null
  */
object AvoidNull extends Rule {
  override def name: String = "AvoidNull"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect { case t: Lit.Null =>
      Issue(
        "Style: Avoid using 'null'. Use 'Option[T]' for optional values to prevent NullPointerException.",
        t.pos
      )
    }
  }
}
