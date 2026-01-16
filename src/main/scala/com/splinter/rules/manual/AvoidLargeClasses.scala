package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/**
 * Rule to detect very large classes, objects, or traits.
 * Large types often indicate a violation of the Single Responsibility Principle.
 */
object AvoidLargeClasses extends Rule {
  override def name: String = "AvoidLargeClasses"
  private val MaxLines = 500

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case t: Defn.Class if isTooLong(t) =>
        mkIssue("Class", t.name.value, lineCount(t), t.pos)
      case t: Defn.Object if isTooLong(t) =>
        mkIssue("Object", t.name.value, lineCount(t), t.pos)
      case t: Defn.Trait if isTooLong(t) =>
        mkIssue("Trait", t.name.value, lineCount(t), t.pos)
    }
  }

  private def lineCount(t: Tree): Int = t.pos.endLine - t.pos.startLine

  private def isTooLong(t: Tree): Boolean = lineCount(t) > MaxLines

  private def mkIssue(kind: String, name: String, lines: Int, pos: Position): Issue = {
    Issue(
      s"Maintainability: $kind '$name' is too long ($lines lines). Consider refactoring to improve cohesion (limit: $MaxLines).",
      pos
    )
  }
}