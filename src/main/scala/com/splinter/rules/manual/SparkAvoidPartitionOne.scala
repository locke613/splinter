package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/**
 * Rule to detect usage of repartition(1) or coalesce(1).
 * 
 * repartition(1): Forces a full shuffle to a single executor, causing OOMs and performance bottlenecks.
 * coalesce(1): Forces upstream stages to run on a single thread (loss of parallelism).
 * 
 * Ref: https://community.databricks.com/t5/technical-blog/top-10-code-mistakes-that-degrade-your-spark-performance/ba-p/118468#toc-hId-1420604381
 */
object SparkAvoidPartitionOne extends Rule {
  override def name: String = "SparkAvoidPartitionOne"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case t @ Term.Apply(Term.Select(_, Term.Name("repartition")), args) 
        if args.headOption.collect { case Lit.Int(1) => true }.getOrElse(false) =>
        Issue("Performance: Avoid repartition(1). It forces a full shuffle to a single node, causing OOMs and bottlenecks.", t.pos)
      
      case t @ Term.Apply(Term.Select(_, Term.Name("coalesce")), List(Lit.Int(1))) =>
        Issue("Performance: Avoid coalesce(1). It kills parallelism by forcing upstream tasks to run on a single thread.", t.pos)
    }
  }
}