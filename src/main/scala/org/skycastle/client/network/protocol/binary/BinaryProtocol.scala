package org.skycastle.client.network.protocol.binary

import _root_.org.apache.mina.core.buffer.IoBuffer
import org.skycastle.client.messaging.Message
import org.skycastle.client.network.protocol.{MessageProtocol}


/**
 * A binary message encoding protocol.
 *
 * NOTE: This will be called to deserialize messages from the client before it has logged in,
 * so should not allow any security breahces (in particular, instantiated class constructors should not alter game state)
 */
// TODO: Create one that packs commonly used Symbols with lookup tables - server tells client about added aliases
// TODO: We could use a cached buffer array in each protocol that is the size of the maximum allowed size of a message?
// TODO: May there be a situation where we get only half of a message from Mina?  In that case we would need to buffer it or leave it unread or somesuch.
class BinaryProtocol extends MessageProtocol {

  private val serializer: BinarySerializer = new BinarySerializer()

  def decodeMessage(receivedBytes: IoBuffer): List[Message] = {
    var messages: List[Message] = Nil

    // There may be multiple messages in the buffer, decode until it is empty
    while (receivedBytes.hasRemaining) {

      // TODO: Start each message with a byte message type id?  Allows easy adding of protocol related messages such as defining aliases, or introducing new protocol types.  As well as stuff like disconnect, timeout, etc?

      val data = serializer.decode[Message](receivedBytes)
      messages = messages ::: List(data)
    }

    messages
  }

  def encodeMessage(message: Message): IoBuffer = {

    // TODO: Inefficient, this should be thread local?
    val buffer = IoBuffer.allocate(256)
    buffer.setAutoExpand(true)

    serializer.encode(buffer, message)

    buffer.flip()

    buffer
  }

}




