package net.tbennett.gatling.sbt.plugin

import com.excilys.ebi.gatling.core.config.{GatlingPropertiesBuilder, GatlingConfiguration}
import scala.tools.nsc.io._

import scala.tools.nsc.io.Path.string2path

case class GatlingBootstrap() {

  val props = new GatlingPropertiesBuilder
  props.dataDirectory(IDEPathHelper.dataDirectory.toString())
  props.resultsDirectory(IDEPathHelper.resultsDirectory.toString())
  props.requestBodiesDirectory(IDEPathHelper.requestBodiesDirectory.toString())
  props.binariesDirectory(IDEPathHelper.compiledScenarioDirectory.toString())
  props.sourcesDirectory(IDEPathHelper.scenariosDirectory.toString())
  props.dataDirectory(IDEPathHelper.scenariosDirectory.toString())

  val gatlingConfiguration = GatlingConfiguration.setUp(props.build)

}

object IDEPathHelper {

  val projectRootDir = sys.props.getOrElse("user.dir", ".")
  val classDirectory = getClass.getResource("IDEPathHelper.class").getPath
  val compiledScenarioDirectory = File(Path(classDirectory.path)).parent

  val testDir = projectRootDir / "src" / "lt" / "scala"
  val gatlingDir = projectRootDir / "gatling"
  val requestBodiesDirectory = gatlingDir / "requestbodies"
  val scenariosDirectory = testDir / "scenarios/"
  val resultsDirectory = gatlingDir / "results"
  val dataDirectory = gatlingDir / "data"

}