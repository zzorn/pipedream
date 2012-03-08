package org.skycastle.parser.serializers

import org.skycastle.utils.ClassUtils


class SerializersImpl extends Serializers {

  private var _serializers: Map[Class[_], ValueSerializer] = Map()

  def registerSerializer[T <: AnyRef](serialize: T => String, deserialize: String  => T)(implicit kind: Manifest[T]) {
    val wrappedType = ClassUtils.nativeTypeToWrappedType(kind.erasure)
    _serializers += (wrappedType -> new ValueSerializer(wrappedType, serialize.asInstanceOf[AnyRef => String], deserialize))
  }

  def serializers: Map[Class[_], ValueSerializer] = _serializers
}