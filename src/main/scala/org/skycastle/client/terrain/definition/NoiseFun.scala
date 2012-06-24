package org.skycastle.client.terrain.definition

import org.skycastle.utils.{SimplexGradientNoise}
import org.skycastle.utils.{RandomUtils}

/**
 * 
 */
final case class NoiseFun(sizeScale: Double = 1.0,
                          sizeX: Double = 1.0,
                          sizeZ: Double = 1.0,
                          offsetX: Double = 1.0,
                          offsetZ: Double = 1.0,
                          amplitude: Double = 1.0,
                          seed: Long = 42 ) extends Fun2d {

  private val randomXOffs = RandomUtils.randomValue(seed + 75623123) * sizeX * sizeScale
  private val randomZOffs = RandomUtils.randomValue(seed + 67273479) * sizeZ * sizeScale

  def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double = {
    val xp = (x + offsetX + randomXOffs ) / (sizeX * sizeScale)
    val zp = (z + offsetZ + randomZOffs) / (sizeZ * sizeScale)
    SimplexGradientNoise.sdnoise2(xp.toFloat, zp.toFloat, null) * amplitude
//    SimplexNoise.noise(xp, zp) * amplitude
  }
}