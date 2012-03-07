package org.skycastle.functions

import reflect.BeanProperty

/**
 *
 */
class Abs(@BeanProperty var f: Double => Double) extends (Double => Double) {

  def this() {
    this(t => 0.0)
  }

  def apply(v: Double) = math.abs(f(v))
}