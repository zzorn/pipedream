package org.skycastle.utils

import java.util.Random

/**
 * 
 */

object RandomUtils {

  def randomValue(seed: Long, start: Double = 0, end: Double = 1): Double = {
    val r = new Random(seed)
    start + r.nextDouble() * (end - start)
  }


}