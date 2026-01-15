package com.splinter.rules.autofix

import com.splinter.rules.{Rule, Issue, Fix}
import scala.meta._

/** Rule to detect usage of .collect() on Spark datasets. Calling .collect()
  * retrieves all data to the driver, which can cause OutOfMemory errors on
  * large datasets.
  */
object AvoidCollect extends Rule {
  override def name: String = "AvoidCollect"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect { case term @ Term.Select(_, name @ Term.Name("collect")) =>
      val fix = Fix("toLocalIterator", name.pos)
      Issue(
        "Performance: Avoid using .collect() on large datasets as it brings all data to the driver.",
        term.pos,
        Some(fix)
      )
    }
  }
}
