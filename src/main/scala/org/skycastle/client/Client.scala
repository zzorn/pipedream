package org.skycastle.client

import engine.EngineImpl
import entity.EntityServiceImpl
import messaging.MessageHandlerImpl
import network.ClientNetworkingImpl
import region.RegionServiceImpl

/**
 * Main entry point to client application.
 * Services base class contains main method that starts up services.
 */
object Client extends ClientServices {

  val appName = "Skycastle Client"

  val messageHandler = addService(new MessageHandlerImpl(this))
  val networking = addService(new ClientNetworkingImpl(messageHandler))
  val entityService = addService(new EntityServiceImpl())
  val regionService = addService(new RegionServiceImpl(this))
  val engine = addService(new EngineImpl(this))

  /* TODO:
   *
   * - UI updating, widgets, binging inputs
   * - Actions on controlled object
   * - Actions with controlled object on other entities
   * - Client side predictive movement
   * - Sound
   * - Music
   *
   */

}