package org.skycastle.client.network.protocol

import org.apache.mina.core.buffer.IoBuffer
import org.apache.mina.core.session.IoSession
import org.apache.mina.filter.codec._
import org.skycastle.utils.Logging
import org.skycastle.client.messaging.Message

/**
 * A way of encoding and decoding Messages to and from a stream of bytes.
 * Used for communication between client and server.
 */
trait MessageProtocol extends ProtocolCodecFactory with Logging {

  /**
   * Encodes a Data object to a buffer.
   * Throws an Exception if there was some problem.
   */
  def encodeMessage(message: Message): IoBuffer

  /**
   * Decodes a message from a buffer to a Message object.
   * Throws an Exception if there was some problem.
   */
  def decodeMessage(receivedBytes: IoBuffer): List[Message]

  val encoder: ProtocolEncoder = new ProtocolEncoderAdapter {
    def encode(session: IoSession, message: Any, out: ProtocolEncoderOutput) {
      message match {
        case data: Message => out.write(encodeMessage(data))
        case _ => throw new Exception("Unknown type for outgoing message: " + message)
      }
    }
  }

  val decoder: ProtocolDecoder = new ProtocolDecoderAdapter {
    def decode(session: IoSession, in: IoBuffer, out: ProtocolDecoderOutput) {
      try {
        decodeMessage(in) foreach ((d: Message) => out.write(d))
      }
      catch {
        case e: Exception =>
          log.warn("Error while decoding incoming message: " + e.getMessage + ", closing session", e)
          session.close(false)
      }
    }
  }

  def getEncoder(session: IoSession): ProtocolEncoder = encoder

  def getDecoder(session: IoSession): ProtocolDecoder = decoder

}


