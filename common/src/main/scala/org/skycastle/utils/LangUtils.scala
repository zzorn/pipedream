package org.skycastle.utils

/**
 *
 */
object LangUtils {

  def repeat(number: Int)(block: => Unit) {
    var i = 0
    while (i < number) {
      block _
      i += 1
    }
  }

}