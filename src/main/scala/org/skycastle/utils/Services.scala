package org.skycastle.utils

import org.skycastle.client.{ClientServicesImpl, ClientServices}

/**
 * Base class for a simple service registry.
 */
trait Services extends Logging {

  private var _started = false
  private var _stopped = false
  private var _services: List[Service] = Nil

  /**
   * @return name of application.  For logging etc.
   */
  protected def appName: String

  /**
   * @return true if the services have been started, but not stopped.
   */
  def isRunning = _started && !_stopped

  /**
   * @return a list of the registered services, in order of registration.
   */
  def services: List[Service] = _services

  /**
   * Default main method that starts up services
   */
  def main(args: Array[String]) {
    // TODO: Read command line arguments
    startup()
  }


  /**
   * Use this to add a service, so that it gets automatically called by startup and shutdown.
   * @return the added service.
   */
  protected def addService[T <: Service](service: T): T = {
    _services :::= List(service)
    service
  }

  /**
   * Initializes all services.
   */
  final def startup() {
    if (_started) throw new IllegalStateException("Services have already been started.")
    _started = true

    setupLogging()

    log.info(appName  + " starting up")

    onStartup()

    _services foreach {service =>
      log.info("Starting service " + service.serviceName)
      service.startup()
    }

    log.info(appName  + " started")
  }


  /**
   * De-initializes all services.
   */
  final def shutdown() {
    if (_stopped) throw new IllegalStateException("Services have already been stopped.")
    _stopped = true

    log.info(appName  + " shutting down")

    // Shut down in reverse order
    _services.reverse foreach {service =>
      log.info("Stopping service " + service.serviceName)
      service.shutdown()
    }

    onShutdown()

    log.info(appName  + " shut down")
  }

  protected def setupLogging() {
    Logging.initializeLogging()
  }

  /**
   * Called before any service has been started.
   */
  protected def onStartup() {}

  /**
   * Called after all services have been shut down.
   */
  protected def onShutdown() {}

}