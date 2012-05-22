package org.skycastle.client.network.protocol.binary

import org.apache.mina.core.buffer.IoBuffer

/**
 * Base class for serializers that are specialized at encoding and
 * decoding instances of a specific class from a byte buffer.
 */
abstract class TypeSerializer[TYPE](kind_ : Class[TYPE]) {

  type T = TYPE

  var id: Byte = -1

  final def kind: Class[T] = kind_

  final def canSerialize(value: Any): Boolean = kind.isInstance(value)

  /**
   * A name representing the kind of objects decoded and encoded by this protocol.
   */
  final def name = kind.getName

  /**
   * Decodes the next object from the buffer, assuming it is of this type.
   */
  def dec(buffer: IoBuffer): T

  /**
   * Encodes the given object and adds it to the buffer.
   * The value should not be null.
   */
  def enc(buffer: IoBuffer, value: T)

}

