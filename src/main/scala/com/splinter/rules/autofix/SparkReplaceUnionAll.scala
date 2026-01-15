package com.splinter.rules.autofix

import com.splinter.rules.{Rule, Issue, Fix}
import scala.meta._

/** Rule to detect usage of deprecated .unionAll() method. .unionAll() was
  * deprecated in Spark 2.0.0. Use .union() instead.
  */
object SparkReplaceUnionAll extends Rule {
  override def name: String = "SparkReplaceUnionAll"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect { case term @ Term.Select(_, name @ Term.Name("unionAll")) =>
      val fix = Fix("union", name.pos)
      Issue(
        "Deprecation: 'unionAll' is deprecated since Spark 2.0.0. Use 'union' instead.",
        term.pos,
        Some(fix)
      )
    }
  }
}