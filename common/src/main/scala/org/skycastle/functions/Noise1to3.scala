package org.skycastle.functions

import javax.vecmath.Vector3d
import org.skycastle.utils.{SimplexGradientNoise, RandomUtils}
import reflect.BeanProperty

/**
 *
 */

final class Noise1to3(@BeanProperty var frequency: Double = 1.0,
                      @BeanProperty var amplitude: Double = 1.0) extends (Double => (Double, Double, Double)) with SeededFunc{

  private var randomXOffs = 0.0
  private var randomYOffs = 0.0
  private var randomZOffs = 0.0


  def this() {
    this(1.0, 1.0)
  }



  override protected def seedChanged() {
    randomXOffs = RandomUtils.randomOffset(seed + 546261)
    randomYOffs = RandomUtils.randomOffset(seed + 789453)
    randomZOffs = RandomUtils.randomOffset(seed + 281346)
  }

  def apply(t: Double): (Double, Double, Double) =  {
    (SimplexGradientNoise.sdnoise1((t + randomXOffs) * frequency) * amplitude,
     SimplexGradientNoise.sdnoise1((t + randomYOffs) * frequency) * amplitude,
     SimplexGradientNoise.sdnoise1((t + randomZOffs) * frequency) * amplitude)
  }

}
