package be.nextlab.gatling.sbt.plugin

import org.scalatools.testing._

import com.excilys.ebi.gatling.charts.report.ReportsGenerator
import com.excilys.ebi.gatling.charts.config.ChartsFiles._
import com.excilys.ebi.gatling.core.result.message.RunRecord
import org.joda.time.DateTime._
import com.excilys.ebi.gatling.core.runner.Runner

import com.excilys.ebi.gatling.core.Predef._

import GatlingFingerprints._

class GatlingFramework extends Framework {
	
	GatlingBootstrap(
		sys.props.get("sbt.gatling.conf.file").get, 
		sys.props.get("sbt.gatling.result.dir").get
	)

	def name = "gatling"

	def tests = Array[Fingerprint](simulationClass, simulationModule)

	def testRunner(testClassLoader:ClassLoader, loggers:Array[Logger]) = new TestInterfaceGatling(testClassLoader, loggers)
}

trait GatlingSimulationFingerprint extends TestFingerprint {
  def superClassName = "be.nextlab.gatling.sbt.plugin.Simulation"
}

object GatlingFingerprints {

	val simulationClass  = new GatlingSimulationFingerprint(){def isModule = false}
	val simulationModule = new GatlingSimulationFingerprint(){def isModule = true}

}

class TestInterfaceGatling(loader: ClassLoader, val loggers: Array[Logger]) extends org.scalatools.testing.Runner2 {
  //  import reflect.Classes._

	def run(className: String, fingerprint: Fingerprint, handler: EventHandler, args: Array[String]) = {
		println("----- Test found -------")
		fingerprint match {
			case tf : TestFingerprint => runTest(className, tf, handler, args)
			//case x => //todo not supported
		}
	}
	

	def runTest(className: String, fingerprint: TestFingerprint, handler: EventHandler, args: Array[String]) =
    	fingerprint match {
      		case f if f.superClassName == simulationClass.superClassName => runSimulation(className, handler, args)
      		//todo case x                                       => 
    	}

  def runSimulation(className:String,  handler: EventHandler, args: Array[String]) = 
  	gatling(createInstanceFor[Simulation](loadClassOf(className, loader)))



  private def createInstanceFor[T <: AnyRef](klass: Class[T])(implicit m: ClassManifest[T]) = {
      val constructor = klass.getDeclaredConstructors()(0)
      constructor.setAccessible(true)
      try {
          val instance: AnyRef = constructor.newInstance().asInstanceOf[AnyRef]
          if (!m.erasure.isInstance(instance)) {
          	error(instance + " is not an instance of " + m.erasure.getName)
          }
          instance.asInstanceOf[T]
      } catch {
          case e: java.lang.reflect.InvocationTargetException => throw e
      }
  }
  
  private def loadClassOf[T <: AnyRef](className: String = "", loader: ClassLoader = Thread.currentThread.getContextClassLoader): Class[T] = 
      loader.loadClass(className).asInstanceOf[Class[T]]

  def gatling(s: Simulation) {
    println("Creating run record")
    val runInfo = new RunRecord(now, "run-test", "stress-test")
    println("Run record created > run scenario")

    s.pre

    val configurations = s()
    new Runner(runInfo, configurations).run

    s.post

    println("Simulation Finished.")
    runInfo.runUuid

    println("scenarion ran > generate reports")
    generateReports(runInfo.runUuid)
    println("reports generated")
  }

  def generateReports(runUuid: String) {
    println("Generating reports...")
    val start = System.currentTimeMillis
    ReportsGenerator.generateFor(runUuid)
    println("Reports generated in " + (System.currentTimeMillis - start) / 1000 + "s.")
  }

}