package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/**
 * Rule to detect usage of .union() or .unionAll() inside loops.
 * Iterative unioning creates a long lineage graph which can cause StackOverflowErrors
 * or performance issues. Convert the collection to a DataFrame and use join instead.
 * 
 * Ref: https://medium.com/@david.mudrauskas/looping-over-spark-an-antipattern-e10ac54824a0
 */
object SparkAvoidUnionInLoop extends Rule {
  override def name: String = "SparkAvoidUnionInLoop"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case t @ Term.Apply(Term.Select(_, Term.Name("union" | "unionAll")), _) if isInsideLoop(t) =>
        Issue(
          "Performance: Avoid calling .union() inside a loop. This creates a large lineage graph. Convert the collection to a DataFrame and use join instead.",
          t.pos
        )
    }
  }

  @scala.annotation.tailrec
  private def isInsideLoop(t: Tree): Boolean = {
    t.parent match {
      case Some(node) => node match {
        case _: Term.For | _: Term.While | _: Term.Do => true
        case Term.Apply(Term.Select(_, Term.Name("foreach" | "map" | "flatMap" | "foldLeft" | "reduce")), _) => true
        case _ => isInsideLoop(node)
      }
      case None => false
    }
  }
}