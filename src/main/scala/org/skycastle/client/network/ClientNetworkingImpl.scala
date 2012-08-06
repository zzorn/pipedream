package org.skycastle.client.network

import org.apache.mina.transport.socket.nio.NioSocketConnector
import protocol.binary.BinaryProtocol
import org.skycastle.client.messaging.Message
import org.apache.mina.core.session.IoSession
import java.net.InetSocketAddress
import org.skycastle.utils.Logging
import org.apache.mina.filter.codec.ProtocolCodecFilter
import org.apache.mina.filter.logging.LoggingFilter
import org.apache.mina.core.service.IoHandlerAdapter
import org.skycastle.client.messaging.MessageHandler

/**
 * Client side network logic.
 * Takes a ServerHandler as parameter.  For now a client can only be connected to one server.
 */
// NOTE: If we want to connect to several servers from one client, pass in some ServerHandlerFactory instead.
class ClientNetworkingImpl(serverHandler: MessageHandler) extends ClientNetworking with Logging {

  private var connector: NioSocketConnector = null
  private var session: IoSession = null

  val connectionTimeout = 10 // conf[Int]("ti", "timeout", 10, "Seconds before aborting a connection attempt when there is no answer.")
  val logMessages = true //conf[Boolean]("lm", "log-messages", false, "Wether to log all network messages.  Only recommended for debugging purposes.")

  private class SessionHandler(serverHandler: MessageHandler) extends IoHandlerAdapter {
    override def sessionOpened(session: IoSession) { serverHandler.onConnected() }
    override def messageReceived(session: IoSession, message: Any) { serverHandler.onMessage(message.asInstanceOf[Message]) }
    override def sessionClosed(session: IoSession) { serverHandler.onDisconnected("Session closed", null) }
    override def exceptionCaught(session: IoSession, cause: Throwable) { log.warn("Exception when handling network connection", cause) }
  }

  /**
   * Attempts to connect to the specified server, using an existing account.
   */
  def login(serverAddress: String,
              serverPort: Int,
              account: String,
              password: String) {
    connect(serverAddress, serverPort)

    // Login
    sendMessage(new Message('login, Map('account -> account, 'pw -> new String(password))))
  }

  /**
   * Attempts to connect to the specified server, creating a new account.
   */
  def createAccount(serverAddress: String,
              serverPort: Int,
              account: String,
              password: String) {
    connect(serverAddress, serverPort)

    // Create account
    sendMessage(new Message('createAccount, Map('account -> account, 'pw -> new String(password))))
  }


  def connect(serverAddress: String, serverPort: Int) {
    if (session != null) throw new IllegalStateException("Session has already been created")

    val connectFuture = connector.connect(new InetSocketAddress(serverAddress, serverPort))
    // TODO: Can't wait while game engine / ui is running - need to use listening, and some state management
    connectFuture.awaitUninterruptibly()
    session = connectFuture.getSession
  }

  def disconnect() {
    if (session != null) {
      val closeFuture = session.close(false)
      // closeFuture.awaitUninterruptibly // NOTE: Can't wait for session to close while in game engine update.
      session = null
    }
  }

  def sendMessage(message: Message) {
    session.write(message)
  }


  override def startup() {
    connector = new NioSocketConnector()
    connector.setConnectTimeoutMillis(connectionTimeout * 1000)
    // TODO: Add encryption filter
    connector.getFilterChain.addLast( "codec", new ProtocolCodecFilter(new BinaryProtocol()))
    if (logMessages) connector.getFilterChain.addLast( "logger", new LoggingFilter() )

    connector.setHandler(new SessionHandler(serverHandler))
  }

  override def shutdown() {
    if (session != null) {
      val closeFuture = session.close(true)
      closeFuture.awaitUninterruptibly
      session = null
    }
  }


}

