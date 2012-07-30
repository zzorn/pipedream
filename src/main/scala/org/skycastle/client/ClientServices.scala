package org.skycastle.client

import messaging.MessageHandler
import network.ClientNetworking
import org.skycastle.utils.Services
import terrain.TerrainService

/**
 * The services that the client is composed of.
 */
trait ClientServices extends Services {

  def networking: ClientNetworking

  def messageHandler: MessageHandler

  def terrain: TerrainService



}