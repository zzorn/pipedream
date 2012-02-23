package org.skycastle.client.terrain.definition

import org.skycastle.utils.SimplexNoise
import org.skycastle.client.util.SimplexGradientNoise

/**
 * 
 */
final case class NoiseFun(sizeX: Double = 10.0,
                          sizeZ: Double = 10.0,
                          offsetX: Double = 1.0,
                          offsetZ: Double = 1.0,
                          amplitude: Double = 1.0) extends Fun2d {
  def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double = {
    val xp = (x + offsetX) / sizeX
    val zp = (z + offsetZ) / sizeZ
    SimplexGradientNoise.sdnoise2(xp.toFloat, zp.toFloat, null) * amplitude
//    SimplexNoise.noise(xp, zp) * amplitude
  }
}