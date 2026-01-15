package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/**
 * Rule to detect interactive/debugging functions that should not be in production.
 * - collect(): Brings all data to driver (OOM risk).
 * - display(): Databricks notebook specific, not for batch jobs.
 * - print(df.count()): Triggers expensive actions for logging.
 * 
 * Ref: https://community.databricks.com/t5/technical-blog/top-10-code-mistakes-that-degrade-your-spark-performance/ba-p/118468#toc-hId-1420604381
 */
object SparkAvoidInteractiveFunctions extends Rule {
  override def name: String = "SparkAvoidInteractiveFunctions"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case t @ Term.Apply(Term.Select(_, Term.Name("collect")), Nil) =>
        Issue("Performance: Avoid using .collect() on large datasets as it brings all data to the driver.", t.pos)
      
      case t @ Term.Apply(Term.Name("display"), _) =>
        Issue("Style: Avoid using display() in production code. It is meant for interactive notebooks.", t.pos)

      case t @ Term.Apply(Term.Name("print" | "println"), args) 
        if args.exists(arg => arg.collect { case Term.Select(_, Term.Name("count")) => true }.nonEmpty) =>
          Issue("Performance: Avoid printing .count() in production. It triggers an expensive full action.", t.pos)
    }
  }
}