name := "splinter"

version := "0.1.0"

scalaVersion := "2.13.13"

libraryDependencies ++= Seq(
  "org.scalameta" %% "scalameta" % "4.9.3",
  "com.github.scopt" %% "scopt" % "4.1.0"
)

Compile / unmanagedSources / excludeFilter := HiddenFileFilter || "demo.scala"