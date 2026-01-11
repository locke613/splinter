package com.splinter.rules.manual

import com.splinter.rules.{Rule, Issue}
import scala.meta._

/**
 * Rule to detect catching 'Throwable'.
 * Catching Throwable intercepts fatal errors (OOM, StackOverflow) which leaves the JVM in a zombie state.
 * Use 'case NonFatal(e) =>' instead.
 * https://github.com/alexandru/scala-best-practices/blob/master/sections/2-language-rules.md#28-must-not-catch-throwable-when-catching-exceptions
 */
object AvoidCatchingThrowable extends Rule {
  override def name: String = "AvoidCatchingThrowable"

  override def check(tree: Tree): Seq[Issue] = {
    tree.collect {
      case c @ Case(Pat.Typed(_, Type.Name("Throwable")), _, _) =>
        Issue("Reliability: Do not catch 'Throwable'. This catches fatal errors like OutOfMemoryError. Use 'import scala.util.control.NonFatal' and 'case NonFatal(e) =>' instead.", c.pos)
      case c @ Case(Type.Name("Throwable"), _, _) => 
        Issue("Reliability: Do not catch 'Throwable'. This catches fatal errors like OutOfMemoryError. Use 'import scala.util.control.NonFatal' and 'case NonFatal(e) =>' instead.", c.pos)
    }
  }
}