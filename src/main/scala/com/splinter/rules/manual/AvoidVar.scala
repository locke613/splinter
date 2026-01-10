package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

object AvoidVar extends Rule {
  override def name: String = "AvoidVar"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case t: Defn.Var =>
        Issue("Style: Avoid mutable variables ('var'). Prefer immutable values ('val').", t.pos)
    }
  }
}