package org.skycastle

import guice.SkycastleModule
import services.scheduler.Scheduler
import util.Logging
import org.apache.log4j.PropertyConfigurator
import actors.Scheduler
import com.google.inject.{Injector, Guice}
import org.skycastle.util.Logging
import org.skycastle.guice.SkycastleModule
import org.skycastle.services.scheduler.Scheduler

/**
 * Starts a new server.
 */
object Server extends Logging {

  def main(args: Array[String]) {

    initializeLogging()

    log.info("Starting Skycastle Server")

    log.info("  Configuring guice")
    val injector: Injector = Guice.createInjector(new SkycastleModule())

    // TODO: Read config from command line parameters or default location

    // TODO: Load previous state from data storage

    // TODO: Find other servers in the cluster and connect to them, listening to incoming events

    // TODO: Start listening for connecting clients, and running simulation update code.

    val scheduler: Scheduler = injector.getInstance(classOf[Scheduler])

    // Testing scheduler..
    scheduler.addRegularTask(interval = 2) {
      duration => println("Tick " + duration)
    }
    scheduler.addRegularTask(1, 2) {
      duration => println("Tock " + duration)
    }
    scheduler.addSingleTask(10) {
      println("Bongg!");
      Thread.sleep(2235)
    }
    scheduler.addVariableTask(3) {
      duration => println("Brick! " + duration); (math.random * 10).floatValue()
    }
    scheduler.loop()

  }

}


