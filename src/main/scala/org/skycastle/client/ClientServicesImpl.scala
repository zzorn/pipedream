package org.skycastle.client

import messaging.MessageHandlerImpl
import network.ClientNetworkingImpl
import terrain.TerrainServiceImpl

/**
 *
 */
class ClientServicesImpl extends ClientServices {

  val messageHandler = addService(new MessageHandlerImpl(this))

  val networking = addService(new ClientNetworkingImpl(messageHandler))

  val terrain = addService(new TerrainServiceImpl())
}