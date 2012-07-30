package org.skycastle.client.messaging

import org.skycastle.client.ClientServices

/**
 *
 */
// TODO: Allow delegating messages to services, where they can call methods that are annotated to be callable with message.
class MessageHandlerImpl(services: ClientServices) extends MessageHandler {

  def onConnectionFailed(reason: String, cause: Exception) {}

  def onConnected() {}

  def onMessage(message: Message) {}

  def onDisconnected(reason: String, cause: Exception) {}
}