package com.splinter.rules.autofix

import com.splinter.rules.{Rule, Issue, Fix}
import scala.meta._

object SparkAvoidCountZero extends Rule {
  override def name: String = "SparkAvoidCountZero"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      // Detect: rdd.count() == 0
      case term @ Term.ApplyInfix(
            Term.Apply(Term.Select(qual, Term.Name("count")), Nil),
            Term.Name("=="),
            Nil,
            List(Lit.Int(0))
          ) =>
        Issue(
          "Performance: Use .isEmpty() instead of .count() == 0",
          term.pos,
          Some(Fix(s"${qual.syntax}.isEmpty()", term.pos))
        )

      // Detect: 0 == rdd.count()
      case term @ Term.ApplyInfix(
            Lit.Int(0),
            Term.Name("=="),
            Nil,
            List(Term.Apply(Term.Select(qual, Term.Name("count")), Nil))
          ) =>
        Issue(
          "Performance: Use .isEmpty() instead of .count() == 0",
          term.pos,
          Some(Fix(s"${qual.syntax}.isEmpty()", term.pos))
        )
    }
  }
}
