package org.skycastle.util

import org.slf4j.{Logger, LoggerFactory}
import org.apache.log4j.PropertyConfigurator

/**
 * A mixin that provides various logging methods that delegate to the SLF4J framework.
 */
trait Logging {

  def loggingPath = getClass

  val log: Logger = LoggerFactory.getLogger(loggingPath)

  /**
   * Should be called from entry point / main class one as the application starts up, to configure the logging used.
   */
  def initializeLogging() {
    // Setup log4j logging using config file
    PropertyConfigurator.configure("log4j.properties")
  }

}
