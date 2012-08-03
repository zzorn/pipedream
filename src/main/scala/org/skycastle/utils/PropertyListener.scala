package org.skycastle.utils

/**
 *
 */
trait PropertyListener {
  def apply(propertyName: Symbol, obj: Any, oldValue: Any, newValue: Any)
}