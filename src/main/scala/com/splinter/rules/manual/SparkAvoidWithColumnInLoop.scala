package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/**
 * Rule to detect usage of .withColumn() inside loops.
 * Calling .withColumn() in a loop creates a new DataFrame and a new Project node
 * in the query plan for each iteration. This leads to a massive lineage graph,
 * causing performance degradation and potential StackOverflowErrors during planning.
 * 
 * Use .select(), .withColumns() (Spark 3.3+), or selectExpr() instead.
 * 
 * Ref: https://community.databricks.com/t5/technical-blog/top-10-code-mistakes-that-degrade-your-spark-performance/ba-p/118468#toc-hId-1420604381
 */
object SparkAvoidWithColumnInLoop extends Rule {
  override def name: String = "SparkAvoidWithColumnInLoop"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case t @ Term.Apply(Term.Select(_, Term.Name("withColumn")), _) if isInsideLoop(t) =>
        Issue(
          "Performance: Avoid calling .withColumn() inside a loop. This creates a deep lineage graph (DAG explosion). Use .select() or .withColumns() instead.",
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