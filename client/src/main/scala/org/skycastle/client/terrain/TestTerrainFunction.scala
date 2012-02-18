package org.skycastle.client.terrain

import com.jme3.math.{Vector3f, Vector2f}


/**
 * 
 */
class TestTerrainFunction extends TerrainFunction {

  val up = new Vector3f(0, 1, 0)

  def height(x: Double, z: Double): Double = {
    math.sin(x * 1.0 / 5.23) * 0.8543 +
    math.sin(z * 1.0 / 5.123) * 0.634 +
    math.sin(z * 1.0 / 84.23) * 30 +
    math.sin(x * 1.0 / 103.34) * 40 +
    math.sin(z * 1.0 / 1084.23) * 150 +
    math.sin(x * 1.0 / 1403.34) * 100
  }

  def normal(x: Double, y: Double) = up


}