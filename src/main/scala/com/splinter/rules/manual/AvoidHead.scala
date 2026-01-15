package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/** Rule to detect usage of Seq.head. Calling .head on a Seq throws
  * NoSuchElementException if the collection is empty. Use .headOption or
  * pattern matching instead.
  * https://github.com/alexandru/scala-best-practices/blob/master/sections/2-language-rules.md#220-must-not-use-seqhead
  */
object AvoidHead extends Rule {
  override def name: String = "AvoidHead"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case t @ Term.Select(_, Term.Name("head")) if !t.parent.exists {
            case Term.Apply(_, args) => args.nonEmpty
            case _                   => false
          } =>
        Issue(
          "Style: Avoid using .head. It throws NoSuchElementException if the collection is empty. Use .headOption instead.",
          t.pos
        )
    }
  }
}
