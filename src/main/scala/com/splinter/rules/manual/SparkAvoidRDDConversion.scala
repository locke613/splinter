package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/**
 * Rule to detect conversion from DataFrame/Dataset to RDD.
 * Converting to RDD breaks the Catalyst Optimizer and Tungsten execution engine,
 * leading to significant performance degradation due to serialization overhead
 * and loss of optimizations.
 */
object SparkAvoidRDDConversion extends Rule {
  override def name: String = "SparkAvoidRDDConversion"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case t @ Term.Select(_, Term.Name("rdd")) =>
        Issue(
          "Performance: Avoid converting DataFrame/Dataset to RDD. This breaks Catalyst optimizations and adds serialization overhead.",
          t.pos
        )
    }
  }
}