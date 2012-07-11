package be.nextlab.gatling.sbt.plugin

import com.excilys.ebi.gatling.core.Predef.{Simulation => GSimulation, _}
import com.excilys.ebi.gatling.core.scenario.configuration.ScenarioConfigurationBuilder

trait Simulation extends GSimulation {
	
	def pre:Unit

	def post:Unit

}