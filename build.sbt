name := "splinter"

version := "0.1.0"

scalaVersion := "2.13.13"

libraryDependencies ++= Seq(
  "org.scalameta" %% "scalameta" % "4.9.3",
  "org.scalameta" %% "scalafmt-dynamic" % "3.7.14",
  "com.github.scopt" %% "scopt" % "4.1.0",
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)

Compile / unmanagedSources / excludeFilter := HiddenFileFilter || "demo.scala"

assembly / assemblyJarName := "splinter.jar"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "services", _*) => MergeStrategy.filterDistinctLines
  case PathList("META-INF", _*) => MergeStrategy.discard
  case "module-info.class" => MergeStrategy.discard
  case _ => MergeStrategy.first
}