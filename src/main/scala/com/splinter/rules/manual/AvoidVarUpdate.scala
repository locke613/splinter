package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/**
 * Rule to detect updates to vars inside loops or conditions.
 * This encourages expression-oriented programming (val result = if...) and avoids Spark closure serialization issues.
 * https://github.com/alexandru/scala-best-practices/blob/master/sections/2-language-rules.md#23-should-not-update-a-var-using-loops-or-conditions
 */
object AvoidVarUpdate extends Rule {
  override def name: String = "AvoidVarUpdate"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case t @ Term.Assign(_, _) if isInsideLoopOrCondition(t) =>
        Issue("Style: Avoid updating vars inside loops or conditions. Use functional transformations (map/fold) or 'val result = if...' expressions.", t.pos)
      
      case t @ Term.ApplyInfix(_, Term.Name(op), _, _) 
        if isAssignmentOp(op) && isInsideLoopOrCondition(t) =>
        Issue(s"Style: Avoid updating vars inside loops or conditions (detected '$op').", t.pos)
    }
  }

  private def isAssignmentOp(op: String): Boolean = {
    op.endsWith("=") && op != "==" && op != "!=" && op != "<=" && op != ">="
  }

  private def isInsideLoopOrCondition(t: Tree): Boolean = {
    var current = t.parent
    while (current.isDefined) {
      val node = current.get
      node match {
        case _: Term.For | _: Term.While | _: Term.Do => return true
        case _: Term.If => return true
        case Term.Apply(Term.Select(_, Term.Name("foreach")), _) => return true
        case _ => 
      }
      current = node.parent
    }
    false
  }
}