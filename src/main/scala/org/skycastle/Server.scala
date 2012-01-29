package org.skycastle

import util.Logging
import world.Scheduler
import org.apache.log4j.PropertyConfigurator

/**
 * Starts a new server.
 */
object Server extends Logging {

  def main(args: Array[ String ]) {

    initializeLogging()

    log.info("Starting Skycastle Server")

    // TODO: Read config from command line parameters or default location

    // TODO: Load previous state from data storage 

    // TODO: Find other servers in the cluster and connect to them, listening to incoming events

    // TODO: Start listening for connecting clients, and running simulation update code.


    // Testing scheduler..
    Scheduler.addRegularTask(interval = 2){duration => println("Tick "+ duration)}
    Scheduler.addRegularTask(1, 2){duration => println("Tock "+duration)}
    Scheduler.addSingleTask(10){println("Bongg!"); Thread.sleep(2235) }
    Scheduler.addVariableTask(3){duration => println("Brick! " + duration); (math.random * 10).floatValue()}
    Scheduler.loop()

  }

}


