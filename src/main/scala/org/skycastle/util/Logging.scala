package org.skycastle.util

import org.slf4j.{Logger, LoggerFactory}

/**
 * A mixin that provides various logging methods that delegate to the SLF4J framework.
 */
trait Logging {
  def loggingPath = getClass

  val log: Logger = LoggerFactory.getLogger(loggingPath)

}
