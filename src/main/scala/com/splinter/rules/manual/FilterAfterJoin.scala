package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/**
 * Rule to detect filters applied immediately after a join.
 * Filtering before joining reduces the dataset size involved in the shuffle, improving performance.
 */
object FilterAfterJoin extends Rule {
  override def name: String = "FilterAfterJoin"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case term @ Term.Apply(
            Term.Select(
              Term.Apply(Term.Select(_, Term.Name("join")), _), 
              Term.Name("filter" | "where")
            ), 
            _
          ) =>
        Issue("Performance: Filter detected after a join. Try to move filters before the join to reduce shuffle size.", term.pos)
    }
  }
}