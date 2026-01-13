package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/**
 * Rule to detect usage of Option.get.
 * Calling .get on an Option (or Try) throws an exception if the value is missing/failed.
 * Note: This rule is syntactic and flags any parameterless .get call.
 * Use .getOrElse, .fold, or .map instead.
 * https://github.com/alexandru/scala-best-practices/blob/master/sections/2-language-rules.md#26-must-not-use-optionget
 *
 * Violation:
 * {{{
 *   val value = option.get
 * }}}
 *
 * Fix:
 * {{{
 *   val value = option.getOrElse("default")
 *   val value = option.fold("default")(v => v.toString)
 *   val value = option.map(_ + 1)
 * }}}
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
        Issue("Style: Avoid using .get (unsafe on Option/Try). It throws exceptions on empty/failure. Use .getOrElse(), .fold(), or .map() instead.", t.pos)
    }
  }
}