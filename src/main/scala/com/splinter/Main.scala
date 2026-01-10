package com.splinter

import com.splinter.rules.autofix.{AvoidCollect, AvoidCountZero, PreferFind, ReplaceUnionAll}
import com.splinter.rules.manual.{AvoidTupleAccess, AvoidDeepNesting, AvoidLongMethods, AvoidLargeTuples, FilterAfterJoin, AvoidReturn, AvoidVar}
import com.splinter.rules.Fix
import java.io.File
import scala.meta._

case class Config(files: Seq[File] = Seq(), fix: Boolean = false)

object Main {
  def main(args: Array[String]): Unit = {
    val parser = new scopt.OptionParser[Config]("splinter") {
      head("splinter", "0.1.0")
      opt[Unit]("fix")
        .action((_, c) => c.copy(fix = true))
        .text("Automatically fix issues")
      arg[File]("<file>...")
        .unbounded()
        .action((x, c) => c.copy(files = c.files :+ x))
        .text("Scala files to analyze")
    }

    parser.parse(args, Config()) match {
      case Some(config) =>
        runLinter(config)
      case None =>
        // arguments are bad, error message will have been displayed
    }
  }

  def runLinter(config: Config): Unit = {
    val rules = Seq(AvoidCollect, AvoidCountZero, AvoidTupleAccess, AvoidDeepNesting, AvoidLongMethods, AvoidLargeTuples, FilterAfterJoin, ReplaceUnionAll, PreferFind, AvoidReturn, AvoidVar)
    
    config.files.foreach { file =>
      if (file.exists() && file.isFile) {
        val path = java.nio.file.Paths.get(file.getAbsolutePath)
        val bytes = java.nio.file.Files.readAllBytes(path)
        val text = new String(bytes, "UTF-8")
        val input = Input.VirtualFile(file.getPath, text)
        
        input.parse[Source] match {
          case Parsed.Success(tree) =>
            val issues = rules.flatMap(_.check(tree))
            
            if (config.fix) {
              val fixes = issues.flatMap(_.fix).sortBy(_.position.start).reverse
              if (fixes.nonEmpty) {
                val fixedText = applyFixes(text, fixes)
                java.nio.file.Files.write(path, fixedText.getBytes("UTF-8"))
                println(s"Fixed ${fixes.size} issues in ${file.getPath}")
              }
              
              val manualIssues = issues.filter(_.fix.isEmpty)
              if (manualIssues.nonEmpty) {
                println(s"The following issues require manual intervention in ${file.getPath}:")
                manualIssues.foreach(println)
              }
            } else {
              issues.foreach(println)
            }
          case Parsed.Error(pos, message, _) =>
            println(s"Error parsing ${file.getPath}: $message at $pos")
        }
      } else {
        println(s"File not found: ${file.getPath}")
      }
    }
  }

  def applyFixes(source: String, fixes: Seq[Fix]): String = {
    var currentText = source
    fixes.foreach { fix =>
      val start = fix.position.start
      val end = fix.position.end
      currentText = currentText.substring(0, start) + fix.replacement + currentText.substring(end)
    }
    currentText
  }
}