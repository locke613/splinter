package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

object AvoidLargeTuples extends Rule {
  override def name: String = "AvoidLargeTuples"
  private val MaxSize = 4

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case t: Term.Tuple if t.args.size > MaxSize =>
        Issue(s"Style: Tuple has ${t.args.size} elements. Consider using a case class.", t.pos)
      case t: Type.Tuple if t.args.size > MaxSize =>
        Issue(s"Style: Tuple type has ${t.args.size} elements. Consider using a case class.", t.pos)
    }
  }
}