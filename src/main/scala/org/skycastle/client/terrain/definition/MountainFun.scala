package org.skycastle.client.terrain.definition

import org.skycastle.utils.SimplexNoise

/**
 * 
 */

final case class MountainFun(size: Double = 100, altitude: Double = 10, sharpness: Double = 2, offset: Double = 1.0) extends Fun2d {
  def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double = {
    val density = 1.0 / size
    val h = math.min(
      SimplexNoise.noise(
        x * density * offset * 1.0234 + 341.123 * offset,
        z * density * offset * 0.99123 + 23.1231 * offset),
      SimplexNoise.noise(
        x * density * offset  * 1.01234 + 567.123 * offset,
        z * density * offset  * 0.998213 + 424.234 * offset))

    math.abs(math.pow(h, sharpness) * altitude)
  }
}