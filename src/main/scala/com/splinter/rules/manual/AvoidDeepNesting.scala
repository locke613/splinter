package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/** Rule to detect deeply nested if-else statements. Deep nesting increases
  * cognitive load and cyclomatic complexity. Suggests refactoring or using
  * pattern matching.
  */
object AvoidDeepNesting extends Rule {
  override def name: String = "AvoidDeepNesting"

  private val MaxDepth = 3

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case t: Term.If if calculateDepth(t) > MaxDepth =>
        Issue(
          s"Style: Avoid nested if-else statements deeper than $MaxDepth layers. Consider refactoring or using pattern matching.",
          t.pos
        )
    }
  }

  private def calculateDepth(t: Tree): Int = {
    var depth = 1 // Start with 1 for the current If
    var current = t

    while (current.parent.isDefined) {
      val parent = current.parent.get
      parent match {
        case p: Term.If =>
          // If we are in the 'else' branch of the parent, it's an 'else-if' chain.
          // We don't count this as increased nesting depth.
          if (p.elsep != current) {
            depth += 1
          }
        case _ =>
        // Not an If parent, just continue up
      }
      current = parent
    }
    depth
  }
}
