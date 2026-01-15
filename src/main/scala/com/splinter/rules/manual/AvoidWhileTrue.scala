package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/**
 * Rule to detect while(true) loops.
 * A (do) while true loop is unlikely to be meant for production code.
 */
object AvoidWhileTrue extends Rule {
  override def name: String = "AvoidWhileTrue"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case t @ Term.While(Lit.Boolean(true), _) =>
        Issue("Avoid while(true) loops.", t.pos)
      case t @ Term.Do(_, Lit.Boolean(true)) =>
        Issue("Avoid do-while(true) loops.", t.pos)
    }
  }
}