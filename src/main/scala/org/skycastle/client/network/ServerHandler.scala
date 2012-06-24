package org.skycastle.client.network

import protocol.Message

/**
 * Something that receives messages or other connection info from a server.
 */
trait ServerHandler {

  def onConnectionFailed(reason: String, cause: Exception)

  def onConnected()

  def onMessage(message: Message)

  def onDisconnected(reason: String, cause: Exception)
}
