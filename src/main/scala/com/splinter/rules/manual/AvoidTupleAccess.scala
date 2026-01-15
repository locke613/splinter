package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/** Rule to detect positional tuple access (e.g. `._1`, `._2`). Positional
  * access reduces readability and makes code brittle to changes in tuple
  * structure.
  */
object AvoidTupleAccess extends Rule {
  override def name: String = "AvoidTupleAccess"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case term @ Term.Select(_, name) if name.value.matches("_\\d+") =>
        Issue(
          "Style: Avoid positional tuple access (e.g. ._1). Use pattern matching or case classes instead.",
          term.pos
        )
    }
  }
}
