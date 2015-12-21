name := "csv-flow"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0",
  "com.github.tototoshi" %% "scala-csv" % "1.2.2"
)