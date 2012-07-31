package org.skycastle.utils

import org.slf4j.{Logger, LoggerFactory}
import org.apache.log4j.PropertyConfigurator
import java.util.Properties

//import org.apache.log4j.PropertyConfigurator

object Logging {

  /**
   * Should be called from entry point / main class one as the application starts up, to configure the logging used.
   */
  def initializeLogging() {
    val properties: Properties = new Properties()

    // Set logging levels and appenders
    properties.setProperty("log4j.rootLogger", "DEBUG, CONSOLE_APPENDER")
    properties.setProperty("org.skycastle", "DEBUG, CONSOLE_APPENDER")

    // Setup pattern for log rows
    properties.setProperty("log4j.appender.CONSOLE_APPENDER", "org.apache.log4j.ConsoleAppender")
    properties.setProperty("log4j.appender.CONSOLE_APPENDER.layout", "org.apache.log4j.PatternLayout")
    properties.setProperty("log4j.appender.CONSOLE_APPENDER.layout.ConversionPattern", "%-4r [%t] %-5p %c %x - %m%n")

    PropertyConfigurator.configure(properties)

    // Setup log4j logging using config file
    //PropertyConfigurator.configure("log4j.properties")
  }
}

/**
 * A mixin that provides various logging methods that delegate to the SLF4J framework.
 */
trait Logging {

  def loggingPath = getClass

  lazy val log: Logger = LoggerFactory.getLogger(loggingPath)

}



