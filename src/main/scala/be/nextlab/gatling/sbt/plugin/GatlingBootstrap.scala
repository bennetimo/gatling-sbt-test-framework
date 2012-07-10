package be.nextlab.gatling.sbt.plugin

import com.excilys.ebi.gatling.core.config.GatlingConfiguration

case class GatlingBootstrap(gatlingFile:String, resultsFolder:String) {


  val gatlingConfiguration = GatlingConfiguration.setUp(
    Some(gatlingFile),
    Some(sys.props("java.io.tmpdir")), //todo dataFolder.getAbsolutePath
    Some(sys.props("java.io.tmpdir")), //todo requestBodiesFolder.getAbsolutePath
    Some(resultsFolder),
    Some(sys.props("java.io.tmpdir"))//todo simulationSourcesFolder.getAbsolutePath
  )

}