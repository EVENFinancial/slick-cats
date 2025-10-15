import sbt._
import sbt.Keys._
import sbt.VirtualAxis
import sbt.internal.ProjectMatrix

/**
  * A virtual axis representing the Slick library version.
  * - directorySuffix like -slick33, -slick34, -slick35
  * - idSuffix derived similarly for unique project ids
  * - moduleName suffix always matches directorySuffix (no carve-out for latest)
  */
final case class SlickVersionAxis(slickVersion: String) extends VirtualAxis.WeakAxis {
  private val parts = slickVersion.split("\\.").toList match {
    case major :: minor :: patch :: _ => (major, minor, patch)
    case major :: minor :: Nil        => (major, minor, "0")
    case major :: Nil                 => (major, "0", "0")
    case _                            => ("0", "0", "0")
  }
  val (major, minor, patch) = parts
  // Updated suffix pattern: -slick<major>-<minor>, e.g. 3.5.2 -> -slick3-5
  override val directorySuffix: String = s"-slick$major-$minor"
  override val idSuffix: String = directorySuffix.replaceAll("""\\W+""", "_")
  def moduleNameSuffix: String = directorySuffix
}

object SlickVersionAxis {
  implicit class ProjectMatrixSlickOps(val m: ProjectMatrix) extends AnyVal {
    /**
      * Add a row for a given Slick version across multiple Scala versions.
      * Provides a convenient place to inject the version-specific Slick dependency and
      * adjust module naming.
      */
    def slickRow(
        slickAxis: SlickVersionAxis,
        scalaVersions: Seq[String],
        settings: Def.SettingsDefinition*
    ): ProjectMatrix =
      m.customRow(
        scalaVersions = scalaVersions,
        axisValues = Seq(slickAxis, VirtualAxis.jvm),
        _.settings(
          // Base module name now without hyphen between slick and cats per user request
          moduleName := "slickcats" + slickAxis.moduleNameSuffix,
          libraryDependencies += ("com.typesafe.slick" %% "slick" % slickAxis.slickVersion),
          Compile / unmanagedSourceDirectories += ((LocalRootProject / baseDirectory).value / "slick-cats" / "src" / "main" / s"slick${slickAxis.major}${slickAxis.minor}" / "scala")
        ).settings(settings: _*)
      )
  }
}
