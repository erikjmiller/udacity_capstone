
enablePlugins(PackPlugin)

name := "SparkKuduLoad"
version := "0.1"
//scalaVersion := "2.12.10"
scalaVersion := "2.11.12"
organization := "com.gracenote"

//lazy val akkaVersion = "2.6.0-M8"
val akkaVersion = "2.5.21"
//val akkaHttpVersion = "10.1.10"
val akkaHttpVersion = "10.0.10"

libraryDependencies ++= Seq(
  // spark core
  "org.apache.spark" %% "spark-core" % "2.4.4",
  "org.apache.spark" %% "spark-sql" % "2.4.4",
  "org.apache.kudu" %% "kudu-spark2" % "1.10.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.8",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.8.8",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.8",

  "com.typesafe.play" %% "play-ahc-ws-standalone" % "2.0.4",
  "com.typesafe.play" %% "play-ws-standalone-json" % "2.0.4",
  "com.typesafe.play" %% "play-json" % "2.7.3",
  "com.github.scopt" % "scopt_2.11" % "3.7.1",

  // spark-modules
  // "org.apache.spark" %% "spark-hive" % "" % "provided",
  // "org.apache.spark" %% "spark-graphx" % "1.6.0",
  // "org.apache.spark" %% "spark-mllib" % "1.6.0",
  // "org.apache.spark" %% "spark-streaming" % "1.6.0",

  // testing
//  "org.scalactic" %% "scalactic" % "3.0.8",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
//  "org.scalacheck"  %% "scalacheck"   % "1.14" % "test",

  // logging
  "org.wvlet.airframe" %% "airframe-log" % "19.10.0",

  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-http"   % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
//  "com.typesafe.akka" %% "akka-actor-testkit" % akkaVersion % Test,

  "com.typesafe" % "config" % "1.3.4",
  "com.lihaoyi" %% "pprint" % "0.5.4"

)

dependencyOverrides += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.8"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.8.8"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.8"

// add additional package resolvers
resolvers += "bintray-spark-packages" at "https://dl.bintray.com/spark-packages/maven/"  // spark
resolvers += "sbt-pack-packages" at "http://repo1.maven.org/maven2/org/xerial/sbt/"  // spark


// set the main class for packaging the main jar
mainClass in (Compile, packageBin) := Some("com.gracenote.cae.sparkkuduload.Main")

// set the main class for the main 'sbt run' task
mainClass in (Compile, run) := Some("com.gracenote.cae.sparkkuduload.Main")

// Compiler settings. Use scalac -X for other options and their description.
// See Here for more info http://www.scala-lang.org/files/archive/nightly/docs/manual/html/scalac.html
scalacOptions ++= List("-feature","-deprecation", "-unchecked", "-Xlint")

// ScalaTest settings.
// Ignore tests tagged as @Slow (they should be picked only by integration test)
testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-l",
  "org.scalatest.tags.Slow", "-u","target/junit-xml-reports", "-oD", "-eS")

logBuffered in Test := false