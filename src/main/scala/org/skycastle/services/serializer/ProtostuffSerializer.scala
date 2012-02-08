package org.skycastle.services.serializer

import com.dyuproject.protostuff.runtime.RuntimeSchema
import com.dyuproject.protostuff.{GraphIOUtil, LinkedBuffer, Schema}
import com.google.inject.Singleton
import java.lang.Class

/**
 * Uses the Protostuff library to serialize objects.
 * The Protostuff library in turn uses Google protocol buffer libraries.
 *
 * NOTE: Protostuff is sensitive to the order of fields - do not change field order in serialized objects!
 */
@Singleton
class ProtostuffSerializer extends Serializer {

  private val buffer = LinkedBuffer.allocate(1*1024*1024);

  def serialize(obj: AnyRef): Array[Byte] = {
    val schema: Schema[AnyRef] = RuntimeSchema.getSchema(obj.getClass.asInstanceOf[Class[AnyRef]])
    return GraphIOUtil.toByteArray(obj, schema, buffer)
  }

  def deserialize[T](data: Array[Byte])(implicit kind: Class[T]): T = {
    val schema: Schema[T] = RuntimeSchema.getSchema(kind)

    val obj: T = kind.newInstance()
    GraphIOUtil.mergeFrom(data, obj, schema)
    return obj
  }

}