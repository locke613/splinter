package com.splinter

import com.splinter.rules.autofix.{
  AvoidCollect,
  AvoidCountZero,
  PreferFind,
  ReplaceUnionAll
}
import com.splinter.rules.manual.{
  AvoidTupleAccess,
  AvoidDeepNesting,
  AvoidLongMethods,
  AvoidLargeTuples,
  FilterAfterJoin,
  AvoidReturn,
  AvoidVar,
  AvoidVarUpdate,
  AvoidCatchingThrowable,
  AvoidNull,
  AvoidOptionGet,
  AvoidHead
}
import com.splinter.rules.{Fix, Issue, Rule}
import java.io.File
import scala.meta._

case class Config(
    files: Seq[File] = Seq(),
    fix: Boolean = false,
    format: Boolean = false
)

object Main {
  def main(args: Array[String]): Unit = {
    val parser = new scopt.OptionParser[Config]("splinter") {
      head("splinter", "0.1.0")
      opt[Unit]("fix")
        .action((_, c) => c.copy(fix = true))
        .text("Automatically fix issues")
      opt[Unit]("format")
        .action((_, c) => c.copy(format = true))
        .text("Format code using Scalafmt")
      help("help").text("prints this usage text")
      arg[File]("<file>...")
        .unbounded()
        .action((x, c) => c.copy(files = c.files :+ x))
        .text("Scala files to analyze")
    }

    parser.parse(args, Config()) match {
      case Some(config) =>
        runLinter(config)
      case None =>
        System.exit(1)
    }
  }

  def runLinter(config: Config): Unit = {
    val rules = Seq(
      AvoidCollect,
      AvoidCountZero,
      AvoidTupleAccess,
      AvoidDeepNesting,
      AvoidLongMethods,
      AvoidLargeTuples,
      FilterAfterJoin,
      ReplaceUnionAll,
      PreferFind,
      AvoidReturn,
      AvoidVar,
      AvoidVarUpdate,
      AvoidCatchingThrowable,
      AvoidNull,
      AvoidOptionGet,
      AvoidHead
    )

    val filesToAnalyze = config.files.flatMap { file =>
      if (file.isDirectory) {
        findAllScalaFiles(file)
      } else if (file.exists()) {
        Seq(file)
      } else {
        println(s"File not found: ${file.getPath}")
        sys.exit(1)
      }
    }

    filesToAnalyze.foreach { file =>
      processFile(file, config, rules)
    }
  }

  def processFile(file: File, config: Config, rules: Seq[Rule]): Unit = {
    if (file.exists() && file.isFile) {
      val path = java.nio.file.Paths.get(file.getAbsolutePath)
      val bytes = java.nio.file.Files.readAllBytes(path)
      val originalText = new String(bytes, "UTF-8")

      val textAfterFormat = if (config.format) {
        val scalafmt = org.scalafmt.interfaces.Scalafmt
          .create(this.getClass.getClassLoader)
        val configPath = java.nio.file.Paths.get(".scalafmt.conf")
        if (configPath.toFile.exists()) {
          scalafmt.format(configPath, path, originalText)
        } else {
          println(
            s"Warning: .scalafmt.conf not found. Skipping formatting for ${file.getPath}"
          )
          originalText
        }
      } else {
        originalText
      }

      val input = Input.VirtualFile(file.getPath, textAfterFormat)

      input.parse[Source] match {
        case Parsed.Success(tree) =>
          val issues = rules.flatMap(_.check(tree))

          val finalText = if (config.fix) {
            val fixes = issues.flatMap(_.fix).sortBy(_.position.start).reverse
            if (fixes.nonEmpty) {
              println(s"Fixed ${fixes.size} issues in ${file.getPath}")
              applyFixes(textAfterFormat, fixes)
            } else {
              textAfterFormat
            }
          } else {
            textAfterFormat
          }

          if (finalText != originalText) {
            java.nio.file.Files.write(path, finalText.getBytes("UTF-8"))
          }

          if (config.fix) {
            val manualIssues = issues.filter(_.fix.isEmpty)
            if (manualIssues.nonEmpty) {
              println(
                s"The following issues require manual intervention in ${file.getPath}:"
              )
              manualIssues.foreach(println)
            }
          } else {
            issues.foreach(issue => println(s"${file.getPath}: $issue"))
          }
        case Parsed.Error(pos, message, _) =>
          println(s"Error parsing ${file.getPath}: $message at $pos")
      }
    }
  }

  def findAllScalaFiles(dir: File): Seq[File] = {
    Option(dir.listFiles()).map(_.toSeq).getOrElse(Seq.empty).flatMap { f =>
      if (f.isDirectory) findAllScalaFiles(f)
      else if (f.getName.endsWith(".scala")) Seq(f)
      else Seq.empty
    }
  }

  def applyFixes(source: String, fixes: Seq[Fix]): String = {
    fixes.foldLeft(source) { (currentText, fix) =>
      val start = fix.position.start
      val end = fix.position.end
      currentText.substring(0, start) + fix.replacement + currentText
        .substring(end)
    }
  }
}
