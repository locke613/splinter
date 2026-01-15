package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/** Rule to detect explicit 'return' statements.
  * https://github.com/alexandru/scala-best-practices/blob/master/sections/2-language-rules.md#21-must-not-use-return
  */
object AvoidReturn extends Rule {
  override def name: String = "AvoidReturn"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect { case t: Term.Return =>
      Issue(
        "Style: Avoid explicit 'return'. In Scala, the last expression in a block is returned automatically.",
        t.pos
      )
    }
  }
}
