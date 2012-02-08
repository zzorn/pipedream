package org.skycastle.services.serializer

/**
 * Used for serialization
 */
// TODO: Support NIO buffers, for network communication etc
trait Serializer {

  def serialize(obj: AnyRef): Array[Byte]

  def deserialize[T](data: Array[Byte])(implicit kind: Class[T]): T


}