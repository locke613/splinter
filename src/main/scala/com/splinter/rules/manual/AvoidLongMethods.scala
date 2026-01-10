package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/**
 * Rule to detect methods that exceed a certain line count threshold.
 * Long methods are harder to read, test, and maintain.
 */
object AvoidLongMethods extends Rule {
  override def name: String = "AvoidLongMethods"
  
  private val MaxLines = 150

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case defn: Defn.Def if (defn.pos.endLine - defn.pos.startLine) > MaxLines =>
        val lines = defn.pos.endLine - defn.pos.startLine
        Issue(s"Style: Method '${defn.name.value}' is too long ($lines lines). Consider refactoring into smaller methods (limit: $MaxLines).", defn.pos)
    }
  }
}