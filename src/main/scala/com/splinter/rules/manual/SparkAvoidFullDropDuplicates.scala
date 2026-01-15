package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/**
 * Rule to detect usage of .dropDuplicates() (without arguments) or .distinct().
 * Applying these on all columns triggers a full shuffle which is expensive.
 * Prefer using .dropDuplicates(Seq("cols")) on specific key columns.
 * 
 * Ref: https://community.databricks.com/t5/technical-blog/top-10-code-mistakes-that-degrade-your-spark-performance/ba-p/118468#toc-hId-1420604381
 */
object SparkAvoidFullDropDuplicates extends Rule {
  override def name: String = "SparkAvoidFullDropDuplicates"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case t @ Term.Apply(Term.Select(_, Term.Name("distinct")), Nil) =>
        Issue(
          "Performance: Avoid using .distinct(). It triggers a full shuffle on all columns. Use .dropDuplicates(Seq(...)) on specific keys instead.",
          t.pos
        )
      case t @ Term.Apply(Term.Select(_, Term.Name("dropDuplicates")), Nil) =>
        Issue(
          "Performance: Avoid using .dropDuplicates() without arguments. It triggers a full shuffle on all columns. Specify key columns.",
          t.pos
        )
    }
  }
}