package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

object AvoidDeepNesting extends Rule {
  override def name: String = "AvoidDeepNesting"
  private val MaxDepth = 3

  override def check(tree: Tree): Seq[Issue] = {
    traverse(tree, 0)
  }

  private def traverse(tree: Tree, depth: Int): Seq[Issue] = {
    tree match {
      case t: Term.If =>
        val currentDepth = depth + 1
        val issue = if (currentDepth > MaxDepth) {
          Seq(Issue(s"Maintainability: Avoid nesting deeper than $MaxDepth layers.", t.pos))
        } else {
          Nil
        }

        val condIssues = traverse(t.cond, depth)
        val thenIssues = traverse(t.thenp, currentDepth)
        val elseIssues = t.elsep match {
          case elseIf: Term.If => traverse(elseIf, depth)
          case other           => traverse(other, currentDepth)
        }
        issue ++ condIssues ++ thenIssues ++ elseIssues

      case t =>
        t.children.flatMap(traverse(_, depth))
    }
  }
}