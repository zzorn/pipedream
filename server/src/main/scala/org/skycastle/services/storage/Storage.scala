package org.skycastle.services.storage

/**
 *
 */
trait Storage {

  def set(key: String, value: AnyRef)

  def get[T](key: String)(implicit kind: Class[T]): T


}