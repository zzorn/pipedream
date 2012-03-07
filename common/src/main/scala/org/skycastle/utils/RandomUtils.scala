package org.skycastle.utils

import java.util.Random

/**
 *
 */

object RandomUtils {

  def randomSeed: Long = new Random().nextLong()

  def randomValue(seed: Long, start: Double = 0, end: Double = 1): Double = {
    val r = new Random(seed)
    start + r.nextDouble() * (end - start)
  }

  def randomOffset(seed: Long): Double = {
    val r = new Random(seed)
    val dividend: Double = r.nextDouble()
    if (dividend != 0) r.nextDouble() + 1.0 / dividend
    else r.nextDouble()
  }


}