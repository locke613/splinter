package com.splinter.rules.autofix

import com.splinter.rules.{Rule, Issue, Fix}
import scala.meta._

/**
 * Rule to detect usage of .filter(...).headOption.
 * Using .find(...) is more efficient as it stops processing after the first match.
 */
object PreferFind extends Rule {
  override def name: String = "PreferFind"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case term @ Term.Select(
            apply @ Term.Apply(
              Term.Select(_, filterName @ Term.Name("filter")), 
              _
            ), 
            headOptionName @ Term.Name("headOption")
          ) =>
        
        // Calculate the text for arguments (e.g., "(x => x > 0)")
        // We extract the substring from the end of 'filter' to the end of the apply call.
        val argsText = apply.pos.text.substring(filterName.pos.end - apply.pos.start)
        val replacement = "find" + argsText
        
        // The fix covers the range from the start of 'filter' to the end of 'headOption'
        val fixPos = Position.Range(term.pos.input, filterName.pos.start, headOptionName.pos.end)
        
        val fix = Fix(replacement, fixPos)
        Issue("Performance: Use .find() instead of .filter().headOption to avoid traversing the entire collection.", term.pos, Some(fix))
    }
  }
}