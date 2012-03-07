package org.skycastle.functions

import org.skycastle.utils.{RandomUtils, SimplexGradientNoise}
import reflect.BeanProperty

/**
 *
 */

final class Noise1to1(@BeanProperty var frequency: Double = 1.0,
                      @BeanProperty var amplitude: Double = 1.0,
                      @BeanProperty var offset: Double = 0.0) extends (Double => Double) with SeededFunc {

  private var randomXOffs = 0.0

  def this() {
    this(1.0, 1.0)
  }

  override protected def seedChanged() {
    randomXOffs = RandomUtils.randomOffset(seed + 546261)
  }


  def apply(x: Double): Double = SimplexGradientNoise.sdnoise1((x + randomXOffs) * frequency) * amplitude + offset

}
