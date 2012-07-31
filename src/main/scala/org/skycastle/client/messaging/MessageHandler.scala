package org.skycastle.client.messaging

import org.skycastle.utils.Service

/**
 * Something that receives messages or other connection info from a server, and handles them.
 */
trait MessageHandler extends Service {

  def onConnectionFailed(reason: String, cause: Exception)

  def onConnected()

  def onMessage(message: Message)

  def onDisconnected(reason: String, cause: Exception)
}
