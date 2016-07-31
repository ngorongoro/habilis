val ScalaVersion = "2.11.8"

val SupportedScalaVersions = Seq("2.10.6", "2.11.8", "2.12.0-M4")

val BintraySnapshots = Resolver.bintrayRepo("ngorongoro", "snapshots")

val BintrayReleases = Resolver.bintrayRepo("ngorongoro", "releases")

val MavenCentralRepository = "Maven Central Repository" at "http://repo1.maven.org/maven2"

val MavenLocalRepository = "Maven Local Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository"

val commonSettings = Seq(
  name := "habilis",
  organization := "com.github.ngorongoro",
  bintrayOrganization := Some("ngorongoro"),
  scalaVersion := ScalaVersion,
  crossScalaVersions := SupportedScalaVersions,
  scalacOptions := Seq("-deprecation", "-language:_"),
  resolvers ++= Seq(BintraySnapshots, MavenCentralRepository, MavenLocalRepository)
) ++ releaseSettings :+ (ReleaseKeys.crossBuild := true)

val privateSettings = Seq(
  publishArtifact := false,
  publishTo := Some(MavenLocalRepository)
)

val publicSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  publishArtifact in (Compile, packageDoc) := true,
  publishTo := { if (isSnapshot.value) Some(BintraySnapshots) else Some(BintrayReleases) },
  pomIncludeRepository := { _ => false },
  pomExtra := (
    <url>http://github.com/ngorongoro/habilis</url>
    <licenses>
      <license>
        <name>BSD 3-Clause</name>
        <url>https://opensource.org/licenses/BSD-3-Clause</url>
      </license>
    </licenses>
    <scm>
      <url>git@github.com:ngorongoro/habilis.git</url>
      <connection>scm:git:git@github.com:ngorongoro/habilis.git</connection>
    </scm>
    <developers>
      <developer>
        <id>olchovy@gmail.com</id>
        <name>Jeffrey Olchovy</name>
        <url>http://github.com/jeffreyolchovy</url>
      </developer>
    </developers>
  )
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
