import sbt._
import Keys._
import sbtrelease._
import ReleasePlugin._
import ReleaseStateTransformations._

object Habilis extends Build {

  val ScalaVersion = "2.11.8"

  val SupportedScalaVersions = Seq("2.10.6", "2.11.8", "2.12.0-M4")

  val SonatypeSnapshots = "Sonatype Snapshots" at s"https://oss.sonatype.org/content/repositories/snapshots"

  val SonatypeReleases = "Sonatype Releases" at s"https://oss.sonatype.org/service/local/staging/deploy/maven2"

  val MavenCentralRepository = "Maven Central Repository" at "http://repo1.maven.org/maven2"

  val MavenLocalRepository = "Maven Local Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"

  val commonSettings = Seq(
    name := "habilis",
    organization := "com.github.ngorongoro",
    scalaVersion := ScalaVersion,
    crossScalaVersions := SupportedScalaVersions,
    scalacOptions := Seq("-deprecation", "-language:_"),
    resolvers ++= Seq(SonatypeSnapshots, MavenCentralRepository, MavenLocalRepository)
  ) ++ releaseSettings ++ Seq(
    ReleaseKeys.crossBuild := true,
    ReleaseKeys.nextVersion := { (version: String) =>
      Version(version).map(_.bumpBugfix.asSnapshot.string).getOrElse(versionFormatError)
    },
    ReleaseKeys.releaseProcess := Seq(
      checkSnapshotDependencies,
      inquireVersions,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      ReleaseStep(action = Command.process("publishSigned", _), enableCrossBuild = true),
      setNextVersion,
      commitNextVersion,
      ReleaseStep(action = Command.process("sonatypeReleaseAll", _), enableCrossBuild = true),
      pushChanges
    )
  )

  val privateSettings = Seq(
    publishArtifact := false,
    publishTo := Some(MavenLocalRepository)
  )

  val publicSettings = Seq(
    publishMavenStyle := true,
    publishArtifact in Test := false,
    publishArtifact in (Compile, packageDoc) := true,
    pomIncludeRepository := { _ => false },
    publishTo := { if (isSnapshot.value) Some(SonatypeSnapshots) else Some(SonatypeReleases) }
  )

  lazy val root = Project(
    "root",
    file("."),
    settings = commonSettings ++ privateSettings
  ).aggregate(cli)

  lazy val cli = Project(
    "cli",
    file("cli"),
    settings = commonSettings ++ publicSettings ++ Seq(
      name := s"${name.value}-cli",
      libraryDependencies ++= Seq(
        "jline" % "jline" % "2.13",
        "org.scalactic" %% "scalactic" % "2.2.6",
        "org.scalatest" %% "scalatest" % "2.2.6" % "test"
      ),
      libraryDependencies := {
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, minorVersion)) if minorVersion > 10  =>
            libraryDependencies.value :+ "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"
          case _ =>
            libraryDependencies.value
        }
      }
    )
  )
}
