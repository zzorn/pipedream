package org.skycastle.services.storage

import redis.clients.jedis.Jedis
import com.google.inject.Inject
import com.google.inject.Singleton
import org.skycastle.services.serializer.Serializer

/**
 * Uses Jedis to access a Redis database for key-value storage.
 */
@Singleton
class RedisStorage @Inject() (serializer: Serializer) extends Storage{

  private val jedis: Jedis = new Jedis("localhost")

  def set(key: String, value: AnyRef) {
    val data: Array[Byte] = serializer.serialize(value)
    jedis.set(key.getBytes(), data)
  }

  def get[T](key: String)(implicit kind: Class[T]): T = {
    val data: Array[Byte] = jedis.get(key.getBytes())
    serializer.deserialize(data)(kind)
  }
}