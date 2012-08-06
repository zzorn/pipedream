package org.skycastle.client

import engine.EngineImpl
import entity.EntityServiceImpl
import messaging.{Message, MessageHandlerImpl}
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
  val entityService = addService(new EntityServiceImpl(this))
  val regionService = addService(new RegionServiceImpl(this))
  val engine = addService(new EngineImpl(this))

  //todo: network, ui widgets, perceived objects, actions on controlled obj, environment visualization & update


  override protected def onPostStartup() {
    // Add some test entities

    Thread.sleep(4000)

    messageHandler.onMessage(new Message('addEntity, Map('entityId -> 'ball1, 'data -> Map('appearance -> Map('radius -> 100)))))
    messageHandler.onMessage(new Message('setAvatarEntity, Map('entityId -> 'ball1)))

  }
}