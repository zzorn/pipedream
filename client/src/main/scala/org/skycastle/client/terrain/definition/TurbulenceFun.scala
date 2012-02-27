package org.skycastle.client.terrain.definition

import org.skycastle.utils.{RandomUtils, SimplexNoise}


/**
 * 
 */

final case class TurbulenceFun(octaves: Int = 4,
                               frequencyChange: Double = 2,
                               amplitudeChange: Double = 0.5,
                               sizeX: Double = 100.0,
                               sizeZ: Double = 100.0,
                               offsetX: Double = 0.0,
                               offsetZ: Double = 0.0,
                               amplitude: Double = 10.0,
                               seed: Long = 42) extends Fun2d {

  private val randomXOffs = RandomUtils.randomValue(seed + 3645618) * sizeX
  private val randomZOffs = RandomUtils.randomValue(seed + 9045521) * sizeZ

  def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double = {
    val xp = (x + offsetX + randomXOffs) / sizeX
    val zp = (z + offsetZ + randomZOffs) / sizeZ
    var sum = 0.0
    var i = 0
    var frequencyFactor = 1.0
    var amplitudeFactor = 1.0
    while (i < octaves) {
      sum += SimplexNoise.noise(xp* frequencyFactor,
                                zp* frequencyFactor) * amplitudeFactor
      frequencyFactor *= frequencyChange
      amplitudeFactor *= amplitudeChange
      i += 1
    }
    sum * amplitude
  }
}