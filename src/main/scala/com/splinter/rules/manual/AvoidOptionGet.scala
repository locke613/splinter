package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/**
 * Rule to detect usage of Option.get.
 * Calling .get on an Option throws NoSuchElementException if the value is None.
 * Use .getOrElse, .fold, or .map instead.
 * https://github.com/alexandru/scala-best-practices/blob/master/sections/2-language-rules.md#26-must-not-use-optionget
 */
object AvoidOptionGet extends Rule {
  override def name: String = "AvoidOptionGet"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case t @ Term.Select(_, Term.Name("get")) 
        if !t.parent.exists {
          case Term.Apply(_, args) => args.nonEmpty
          case _ => false
        } =>
        Issue("Style: Avoid using Option.get. It throws NoSuchElementException if None. Use .getOrElse(), .fold(), or .map() instead.", t.pos)
    }
  }
}