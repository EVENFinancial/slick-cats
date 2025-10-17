import SlickVersionAxis._
import sbt._
import sbtprojectmatrix.ProjectMatrixPlugin.autoImport._
import sbtrelease.ReleasePlugin.autoImport._


val scala212 = "2.12.19"
val scala213 = "2.13.16"
val scala3   = "3.3.6"

// Only enable this for local testing
//ThisBuild / Test / parallelExecution := false

// Build-wide settings
ThisBuild / organization := "com.rms.miu"
ThisBuild / scalaVersion := scala213

// GitHub Packages publishing configuration
ThisBuild / publishTo := Some("GitHub Package Registry" at "https://maven.pkg.github.com/EVENFinancial/slick-cats")

// GitHub authentication (requires GITHUB_TOKEN environment variable)
ThisBuild / credentials += Credentials(
  "GitHub Package Registry",
  "maven.pkg.github.com",
  "EVENFinancial", // GitHub username
  sys.env.getOrElse("GITHUB_TOKEN", "")
)

// Additional GitHub Packages settings
ThisBuild / publishMavenStyle := true
ThisBuild / versionScheme := Some("early-semver")

Global / excludeLintKeys += publishMavenStyle
ThisBuild / licenses += ("BSD New", url("https://opensource.org/licenses/BSD-3-Clause"))
ThisBuild / homepage := Some(url("https://github.com/EVENFinancial/slick-cats"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/EVENFinancial/slick-cats"),
    "scm:git@github.com:EVENFinancial/slick-cats.git"
  )
)
ThisBuild / developers := List(
  Developer(id = "23will", name = "William Duncan", email = "", url("https://github.com/23will")),
  Developer(id = "tvaroh", name = "Alexander Semenov", email = "", url("https://github.com/tvaroh")),
  Developer(id = "frosforever", name = "Yosef Fertel", email = "", url("https://github.com/frosforever"))
)

// Common (per-project) settings
val commonSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xlint",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard"
  ),
  // Remove options not supported under Scala 3
  scalacOptions := {
    val ver = scalaVersion.value
    val base = scalacOptions.value
    if (ver.startsWith("3."))
      base.filterNot(
        Set(
          "-Xlint",
          "-Ywarn-dead-code",
          "-Ywarn-numeric-widen",
          "-Ywarn-value-discard"
        ).contains
      )
    else base
  }
)

val catsVersion = "2.13.0"

// Define Slick version axes
val slick33 = SlickVersionAxis("3.3.3")
val slick34 = SlickVersionAxis("3.4.1")
val slick35 = SlickVersionAxis("3.5.2")

val slick33ScalaVersions = Seq(scala212, scala213)          // Slick 3.3.x: Scala 2.12 & 2.13 only
val slick34ScalaVersions = Seq(scala212, scala213)          // Slick 3.4.x: Scala 2.12 & 2.13 only (no Scala 3)
val slick35ScalaVersions = Seq(scala212, scala213, scala3)  // Slick 3.5.x: retains Scala 2.12 & adds Scala 3

// sbt-release configuration
import ReleaseTransformations._

ThisBuild / releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  publishArtifacts,
  setNextVersion,
  commitNextVersion,
  pushChanges
)

lazy val slickcats =
  projectMatrix
    .in(file("slick-cats"))
    .settings(commonSettings)
    .settings(
      // Artifact name will be modified by SlickVersionAxis to include version suffix
      name := "slickcats",
      description := "Cats instances for Slick's DBIO",
      libraryDependencies ++= Seq(
        "org.typelevel" %% "cats-core" % catsVersion,
        "org.typelevel" %% "scalac-compat-annotation" % "0.1.4",
        "org.typelevel" %% "cats-laws" % catsVersion % Test,
        "org.typelevel" %% "discipline-scalatest" % "2.3.0" % Test,
        "org.scalatest" %% "scalatest" % "3.2.14" % Test,
        "org.scalacheck" %% "scalacheck" % "1.17.0" % Test,
      )
    )
    .slickRow(slick33, slick33ScalaVersions, publish / skip := false)
    .slickRow(slick34, slick34ScalaVersions, publish / skip := false)
    .slickRow(slick35, slick35ScalaVersions, publish / skip := false)

lazy val docs = (project in file("slick-cats-docs"))
  .enablePlugins(MdocPlugin)
  .dependsOn(LocalProject("slickcats-slick3-53")) // Scala 3 row of Slick 3.5 line
  .settings(commonSettings)
  .settings(
    name := "slick-cats-docs",
    publish / skip := true,
    scalaVersion := scala3,
    scalacOptions --= Seq("-Xfatal-warnings")
  )

// Root aggregator including docs (simple project)
lazy val root = (project in file("."))
  .aggregate((slickcats.projectRefs :+ LocalProject("docs")): _*)
  .settings(
    name := "slick-cats-parent",
    publish / skip := true,
    sourcesInBase := false,
    addCommandAlias("docsAll", "docs/mdoc")
  )
