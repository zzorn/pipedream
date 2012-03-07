package org.skycastle.functions

import org.skycastle.utils.{SimplexGradientNoise, RandomUtils}
import reflect.BeanProperty

/**
 *
 */
final class Noise1to2(@BeanProperty var frequency: Double = 1.0,
                      @BeanProperty var amplitude: Double = 1.0) extends (Double => (Double, Double)) with SeededFunc {

  private var randomXOffs = 0.0
  private var randomYOffs = 0.0

  def this() {
    this(1.0, 1.0)
  }

  override protected def seedChanged() {
    randomXOffs = RandomUtils.randomOffset(seed + 546261)
    randomYOffs = RandomUtils.randomOffset(seed + 789453)
  }

  def apply(t: Double): (Double, Double) =  {
    (SimplexGradientNoise.sdnoise1((t + randomXOffs) * frequency) * amplitude,
      SimplexGradientNoise.sdnoise1((t + randomYOffs) * frequency) * amplitude)
  }

}

