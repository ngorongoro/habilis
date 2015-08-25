import sbt._
import Keys._

object Habilis extends Build {

  val ScalaVersion = "2.10.5"

  val commonSettings = Seq(
    name := "habilis",
    organization := "ngoro.ngoro",
    scalaVersion := ScalaVersion,
    scalacOptions := Seq("-deprecation", "-language:_"),
    resolvers ++= Seq(
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
      "Maven Central Repository" at "http://repo1.maven.org/maven2",
      "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"
    )
  )

  val publishSettings = Seq(
    publishMavenStyle := true,
    publishArtifact in Test := false,
    publishArtifact in (Compile, packageDoc) := false,
    pomIncludeRepository := { _ => false }
  )

  lazy val root = Project(
    "root",
    file(".")
  ).aggregate(cli)

  lazy val cli = Project(
    "cli",
    file("cli"),
    settings = commonSettings ++ publishSettings ++ Seq(
      name := s"${name.value}-cli",
      libraryDependencies ++= Seq(
        "jline" % "jline" % "2.13",
        "org.scalactic" %% "scalactic" % "2.2.4",
        "org.scalatest" %% "scalatest" % "2.2.4" % "test"
      )
    )
  )
}
