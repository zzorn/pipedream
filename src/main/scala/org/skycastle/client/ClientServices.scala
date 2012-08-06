package org.skycastle.client

import engine.Engine
import entity.EntityService
import messaging.MessageHandler
import network.ClientNetworking
import org.skycastle.utils.ServicesBase
import region.RegionService

/**
 * The services that the client is composed of.
 */
trait ClientServices extends ServicesBase {

  def networking: ClientNetworking

  def messageHandler: MessageHandler

  def entityService: EntityService

  def regionService: RegionService

  def engine: Engine

}