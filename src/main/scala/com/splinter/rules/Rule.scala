package com.splinter.rules

import scala.meta._

case class Fix(replacement: String, position: Position)

case class Issue(message: String, position: Position, fix: Option[Fix] = None) {
  override def toString: String =
    s"[${position.startLine}:${position.startColumn}] $message"
}

trait Rule {
  def name: String
  def check(tree: Tree): Seq[Issue]
}
