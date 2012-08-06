package org.skycastle.client.network

import org.skycastle.client.messaging.Message
import org.skycastle.utils.Service

/**
 * Interface for client side networking.
 */
trait ClientNetworking extends Service {

  /**
   * Attempts to connect to the specified server, using an existing account.
   */
  def login(serverAddress: String,
            serverPort: Int,
            account: String,
            password: String)

  /**
   * Attempts to connect to the specified server, creating a new account.
   */
  def createAccount(serverAddress: String,
                    serverPort: Int,
                    account: String,
                    password: String)

  /**
   * Sends a message to the connected server.
   */
  def sendMessage(message: Message)

  /**
   * Disconnects from server.
   */
  def disconnect()
}