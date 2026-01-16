package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/**
 * Rule to detect very large files.
 * Large files often indicate a violation of the Single Responsibility Principle,
 * increase cognitive load, and slow down incremental compilation.
 */
object AvoidLargeFiles extends Rule {
  override def name: String = "AvoidLargeFiles"
  private val MaxLines = 1000

  override def check(tree: Tree): Seq[Issue] = {
    if (tree.is[Source]) {
      val lines = tree.pos.endLine + 1
      if (lines > MaxLines) {
        Seq(Issue(
          s"Maintainability: File is too long ($lines lines). Consider breaking it down into smaller files (limit: $MaxLines).",
          tree.pos
        ))
      } else {
        Nil
      }
    } else {
      Nil
    }
  }
}