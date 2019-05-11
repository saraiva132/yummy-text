ThisBuild / scalaVersion := "2.12.8"
ThisBuild / version := "0.1.0"
ThisBuild / name := "yummy-text"
ThisBuild / organization := "io.yummy.text"
ThisBuild / organizationName := "YummyText"

lazy val `yummy-text` =
  (project in file("."))
    .settings(commonSettings)
    .settings(
      mainClass := Some("io.yummy.text.Main"),
    )
    .configs(IntegrationTest)
    .settings(
      Defaults.itSettings,
      testOptions += Tests.Argument("-oF"),
      parallelExecution in Test := false,
      parallelExecution in IntegrationTest := false,
    )

lazy val library =
  new {
    object Version {
      val cats       = "1.6.0"
      val catsEffect = "1.2.0"
      val http4s     = "0.20.0"
      val fs2        = "1.0.4"
      val circe      = "0.11.1"
      val logback    = "1.2.3"
      val scalaTest  = "3.0.5"
    }

    val CommonDependencies = Seq(
      "org.typelevel"  %% "cats-core"            % Version.cats,
      "org.typelevel"  %% "cats-effect"          % Version.catsEffect,
      "org.http4s"     %% "http4s-dsl"           % Version.http4s,
      "org.http4s"     %% "http4s-blaze-server"  % Version.http4s,
      "org.http4s"     %% "http4s-blaze-client"  % Version.http4s,
      "org.http4s"     %% "http4s-circe"         % Version.http4s,
      "io.circe"       %% "circe-parser"         % Version.circe,
      "io.circe"       %% "circe-literal"        % Version.circe,
      "io.circe"       %% "circe-generic"        % Version.circe,
      "io.circe"       %% "circe-generic-extras" % Version.circe,
      "io.circe"       %% "circe-java8"          % Version.circe,
      "co.fs2"         %% "fs2-core"             % Version.fs2,
      "co.fs2"         %% "fs2-io"               % Version.fs2,
      "ch.qos.logback" % "logback-classic"       % Version.logback,
      "org.scalatest"  %% "scalatest"            % Version.scalaTest % "test, it",
    )
  }

lazy val commonSettings =
  Seq(
    libraryDependencies ++= library.CommonDependencies,
    scalacOptions ++= Seq(
      "-unchecked",
      "-target:jvm-1.8",
      "-deprecation",
      "-encoding",
      "utf-8",
      "-feature",
      "-explaintypes",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-Xcheckinit",
      "-Xfuture",
      "-Xlint",
      "-Xlint:adapted-args",
      "-Xlint:by-name-right-associative",
      "-Xlint:constant",
      "-Xlint:delayedinit-select",
      "-Xlint:doc-detached",
      "-Xlint:inaccessible",
      "-Xlint:infer-any",
      "-Xlint:missing-interpolator",
      "-Xlint:nullary-override",
      "-Xlint:nullary-unit",
      "-Xlint:option-implicit",
      "-Xlint:package-object-classes",
      "-Xlint:poly-implicit-overload",
      "-Xlint:private-shadow",
      "-Xlint:stars-align",
      "-Xlint:type-parameter-shadow",
      "-Xlint:unsound-match",
      "-Ydelambdafy:method",
      "-Yno-adapted-args",
      "-Ypartial-unification",
      "-Ywarn-dead-code",
      "-Ywarn-extra-implicit",
      "-Ywarn-inaccessible",
      "-Ywarn-infer-any",
      "-Ywarn-nullary-override",
      "-Ywarn-nullary-unit",
      "-Ywarn-numeric-widen",
      "-Ywarn-unused:implicits",
      "-Ywarn-unused:imports",
      "-Ywarn-unused:locals",
      "-Ywarn-unused:params",
      "-Ywarn-unused:patvars",
      "-Ywarn-unused:privates",
      "-Ywarn-value-discard",
      "-Xfatal-warnings"
    ),
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4")
  )

scalafmtOnCompile := true
