package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/**
 * Rule to detect try-catch blocks that immediately rethrow caught exceptions.
 * Immediately re-throwing a caught exception is equivalent to not catching it at all.
 */
object CatchExceptionImmediatelyRethrown extends Rule {
  override def name: String = "CatchExceptionImmediatelyRethrown"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case c @ Case(pat, _, Term.Throw(Term.Name(throwName))) =>
        val caughtName = pat match {
          case Pat.Var(Term.Name(name)) => Some(name)
          case Pat.Typed(Pat.Var(Term.Name(name)), _) => Some(name)
          case _ => None
        }

        if (caughtName.contains(throwName)) {
          Some(Issue(
            "Caught exception is immediately rethrown.",
            c.pos
          ))
        } else {
          None
        }
    }.flatten
  }
}