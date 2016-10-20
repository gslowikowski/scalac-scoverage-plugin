import sbt._
import sbt.Keys._
import sbtrelease.ReleasePlugin.autoImport._
import com.typesafe.sbt.pgp.PgpKeys

val Org = "org.scoverage"
val MockitoVersion = "1.10.19"
val ScalatestVersion = "3.0.0"

val appSettings = Seq(
    organization := Org,
    scalaVersion := "2.11.8",
    crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.0-RC1"),
    fork in Test := false,
    publishMavenStyle := true,
    publishArtifact in Test := false,
    parallelExecution in Test := false,
    scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-encoding", "utf8"),
    concurrentRestrictions in Global += Tags.limit(Tags.Test, 1),
    publishTo <<= version {
      (v: String) =>
        val nexus = "https://oss.sonatype.org/"
        if (v.trim.endsWith("-SNAPSHOT"))
          Some(Resolver.sonatypeRepo("snapshots"))
        else
          Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := {
      <url>https://github.com/scoverage/scalac-scoverage-plugin</url>
        <licenses>
          <license>
            <name>Apache 2</name>
            <url>http://www.apache.org/licenses/LICENSE-2.0</url>
            <distribution>repo</distribution>
          </license>
        </licenses>
        <scm>
          <url>git@github.com:scoverage/scalac-scoverage-plugin.git</url>
          <connection>scm:git@github.com:scoverage/scalac-scoverage-plugin.git</connection>
        </scm>
        <developers>
          <developer>
            <id>sksamuel</id>
            <name>Stephen Samuel</name>
            <url>http://github.com/sksamuel</url>
          </developer>
        </developers>
    },
    pomIncludeRepository := {
      _ => false
    }
  ) ++ Seq(
    releaseCrossBuild := true,
    releasePublishArtifactsAction := PgpKeys.publishSigned.value
  )

lazy val root = Project("scalac-scoverage", file("."))
    .settings(name := "scalac-scoverage")
    .settings(appSettings: _*)
    .settings(publishArtifact := false)
    .aggregate(core, plugin, runtime, report)

lazy val core = Project("scalac-scoverage-core", file("scalac-scoverage-core"))
    .settings(name := "scalac-scoverage-core")
    .settings(appSettings: _*)
/*GSTEMP - remove this (rewrite tests for JUnit):
    .settings(libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % ScalatestVersion % "test"
  ))
*/

lazy val runtime = Project("scalac-scoverage-runtime", file("scalac-scoverage-runtime"))
    .settings(name := "scalac-scoverage-runtime")
    .settings(appSettings: _*)
/*GSTEMP - remove this (rewrite tests for JUnit):
    .settings(libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % ScalatestVersion % "test"
  ))
*/

lazy val plugin = Project("scalac-scoverage-plugin", file("scalac-scoverage-plugin"))
    .settings(name := "scalac-scoverage-plugin")
    .settings(appSettings: _*)
    .settings(unmanagedSourceDirectories in Compile += (scalaSource in Compile in core).value) // scalac plugin cannot have dependencies
    .settings(libraryDependencies ++= Seq(
    "org.mockito" % "mockito-all" % MockitoVersion % "test",
/*GSTEMP - remove this (rewrite tests for JUnit):
    "org.scalatest" %% "scalatest" % ScalatestVersion % "test",
*/
    "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided"
  ))/*GSTEMP - remove this (add macros to test harness):
    .settings(libraryDependencies ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, scalaMajor)) if scalaMajor > 10 => Seq(
        "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0" % "test"
      )
      case _ => Seq(
        "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2" % "test"
      )
    }
  })*/

lazy val report = Project("scalac-scoverage-report", file("scalac-scoverage-report"))
    .dependsOn(core)
    .settings(name := "scalac-scoverage-report")
    .settings(appSettings: _*)
/*GSTEMP - remove this (rewrite tests for JUnit):
    .settings(libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % ScalatestVersion % "test"
  ))*/.settings(libraryDependencies ++= {
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, scalaMajor)) if scalaMajor > 10 => Seq(
        "org.scala-lang.modules" %% "scala-xml" % "1.0.5"
      )
      case _ => Nil
    }
  })
