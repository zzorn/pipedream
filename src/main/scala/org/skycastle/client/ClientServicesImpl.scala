package org.skycastle.client

import engine.EngineImpl
import entity.EntityServiceImpl
import messaging.MessageHandlerImpl
import network.ClientNetworkingImpl
import region.RegionServiceImpl
import org.skycastle.utils.Logging

/**
 *
 */
class ClientServicesImpl extends ClientServices {

  val appName = "Skycastle Client"

  val messageHandler = addService(new MessageHandlerImpl(this))

  val networking = addService(new ClientNetworkingImpl(messageHandler))

  val entityService = addService(new EntityServiceImpl())

  val regionService = addService(new RegionServiceImpl(this))

  val engine = addService(new EngineImpl())

}