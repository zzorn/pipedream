package org.skycastle.client.network

/**
 * Interface for client side networking.
 */
trait ClientNetworking {

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
   * Initialize networking service.
   */
  def setup()

  /**
   * Close any open session.
   */
  def shutdown()

}