package org.skycastle

import util.Logging
import world.Scheduler

/**
 * Starts a new server.
 */
object Server extends Logging {

  def main(args: Array[ String ]) {
    log.info("Starting Skycastle Server")

    // TODO: Read config from command line parameters or default location

    // TODO: Load previous state from data storage 

    // TODO: Find other servers in the cluster and connect to them, listening to incoming events

    // TODO: Start listening for connecting clients, and running simulation update code.


    // Testing scheduler..
    Scheduler.addRegularTask(interval = 2000){duration => println("Tick"+ duration)}
    Scheduler.addRegularTask(1000, 2000){duration => println("Tock"+duration)}
    Scheduler.addSingleTask(10000){println("Bongg!"); Thread.sleep(2235) }
    Scheduler.addVariableTask(3000){duration => println("Brick! " + duration); (math.random * 10000).longValue()}
    Scheduler.loop()
  }

}