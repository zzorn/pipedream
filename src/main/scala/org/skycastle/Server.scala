package org.skycastle

import util.Logging

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

  }

}