val scala3Version = "3.3.0"

lazy val root = project
  .in(file("."))
  .settings(
    name := "jumpstart-20231120-exercise",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,
  )

parallelExecution := false

libraryDependencies ++= Seq(
  "com.greenfossil" %% "sqlview2" % "0.6.13",
  "com.greenfossil" %% "commons-json" % "1.0.5",
  "org.slf4j" % "slf4j-api" % "2.0.5",
  "ch.qos.logback" % "logback-classic" % "1.4.7" % Test,
  "org.scalameta" %% "munit" % "0.7.29" % Test
)