package net.tbennett.gatling.sbt.plugin

import org.scalatools.testing._
import com.excilys.ebi.gatling.core.runner.Runner
import com.excilys.ebi.gatling.core.scenario.configuration.Simulation
import java.lang.System._
import com.excilys.ebi.gatling.charts.report.ReportsGenerator
import com.excilys.ebi.gatling.core.result.reader.DataReader

import GatlingFingerprints._
import com.excilys.ebi.gatling.core.result.message.RequestStatus._
import com.excilys.ebi.gatling.core.runner.Selection
import scala.Some

/**
 * Fingerprint to allow sbt unified test interface to recognise Gatling scenario classes as valid tests
 */
trait GatlingSimulationFingerprint extends SubclassFingerprint {
  def superClassName = "com.excilys.ebi.gatling.core.scenario.configuration.Simulation"
}

object GatlingFingerprints {
  val simulationClass  = new GatlingSimulationFingerprint(){def isModule = false}
  val simulationModule = new GatlingSimulationFingerprint(){def isModule = true}
}

/**
 * Hook into sbt test framework for Gatling
 */
class GatlingFramework extends Framework {

  GatlingBootstrap()
  def name = "gatling"
  def tests = Array[Fingerprint](simulationClass, simulationModule)
  def testRunner(testClassLoader: ClassLoader, loggers: Array[Logger]) = new TestInterfaceGatling(testClassLoader, loggers)
}

/**
 * Contains the logic for actually running Gatling simulations
 * @param loader
 * @param loggers
 */
class TestInterfaceGatling(loader: ClassLoader, val loggers: Array[Logger]) extends Runner2 {

  def run(className: String, fingerprint: Fingerprint, handler: EventHandler, args: Array[String]) = {
    println("----- Simulation found -------")
    fingerprint match {
      case tf: SubclassFingerprint => {
        println("Running simulation %s".format(className))
        runSimulation(className, handler, args)
      }
      //case x => //todo not supported
    }
  }

  def runSimulation(className: String, handler: EventHandler, args: Array[String]) = {
    val simulation = loader.loadClass(className).asInstanceOf[Class[Simulation]]
    val selection = new Selection(simulation, className, "")
    val runID = new Runner(selection).run._1
    generateReports(runID)

    val dataReader = DataReader.newInstance(runID)

    //Pull out the stats and notify sbt
    val ok = dataReader.generalStats(Some(OK), None, None)
    val ko = dataReader.generalStats(Some(KO), None, None)

    (1 to ok.count).map(i => handler.handle(OKEvent(className)))
    (1 to ko.count).map(i => handler.handle(KOEvent(className)))
  }

  private def generateReports(outputDirectoryName: String) {
    println("Generating reports...")
    val start = currentTimeMillis
    val indexFile = ReportsGenerator.generateFor(outputDirectoryName, DataReader.newInstance(outputDirectoryName))
    println("Reports generated in " + (currentTimeMillis - start) / 1000 + "s.")
    println("Please open the following file : " + indexFile)
  }

  abstract class ResultEvent(className: String) extends Event {
    def error(): Throwable = null
    def description(): String = ""
    def testName(): String = className
  }

  case class OKEvent(className: String) extends ResultEvent(className) {
    def result = Result.Success
  }
  case class KOEvent(className: String) extends ResultEvent(className) {
    def result = Result.Failure
  }
}