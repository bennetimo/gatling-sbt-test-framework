import sbt._
import Keys._

object GatlingTestFrameworkBuild extends Build {
  val SNAPSHOT = "-SNAPSHOT"

  val buildVersion = "0.0.1-SNAPSHOT"

  lazy val gatlingProject = Project("gatling-sbt-test-framework", file("."), settings = gatlingSettings)

  /* OFFICIAL GATLING REPO */
  val gatlingReleasesRepo = "Gatling Releases Repo" at "http://repository.excilys.com/content/groups/public"
  val gatling3PartyRepo = "Gatling Third-Party Repo" at "http://repository.excilys.com/content/repositories/thirdparty"

  /* GATLING DEPS */
  val gatlingVersionNumber = "1.4.7"
  val gatlingApp = "com.excilys.ebi.gatling" % "gatling-app" % gatlingVersionNumber
  val gatlingCore = "com.excilys.ebi.gatling" % "gatling-core" % gatlingVersionNumber
  val gatlingHttp = "com.excilys.ebi.gatling" % "gatling-http" % gatlingVersionNumber
  val gatlingRecorder = "com.excilys.ebi.gatling" % "gatling-recorder" % gatlingVersionNumber
  val gatlingCharts = "com.excilys.ebi.gatling" % "gatling-charts" % gatlingVersionNumber
  val gatlingHighcharts = "com.excilys.ebi.gatling.highcharts" % "gatling-charts-highcharts" % gatlingVersionNumber //withSources

  lazy val libDependencies = Seq(
    "org.scala-tools.testing" % "test-interface" % "0.5",
    gatlingApp,
    gatlingCore,
    gatlingHttp,
    gatlingRecorder,
    gatlingCharts,
    gatlingHighcharts
  )

  lazy val gatlingSettings = Defaults.defaultSettings ++ Seq(
    version := buildVersion,
    organization := "net.tbennett",
    libraryDependencies ++= libDependencies,
    resolvers ++= Seq(gatlingReleasesRepo, gatling3PartyRepo),
    publishMavenStyle := true,
    //scalaVersion := "2.9.2",
    publishTo := Some(Resolver.mavenLocal)
  )
}

