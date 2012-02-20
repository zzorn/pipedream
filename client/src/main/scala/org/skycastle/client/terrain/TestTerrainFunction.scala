package org.skycastle.client.terrain

import com.jme3.math.{Vector3f, Vector2f}
import org.skycastle.utils.SimplexNoise


/**
 * 
 */
class TestTerrainFunction extends TerrainFunction {

  val up = new Vector3f(0, 1, 0)

  def getHeight(x: Double, z: Double): Double = {
    SimplexNoise.noise(x*0.00013, z*0.000064) * 2000 +
    SimplexNoise.noise(x*0.0001+63554.12, z*0.0001+5672.1) * 1000 +
    SimplexNoise.noise(x*0.002+3454.123, z*0.001+414.123) * 80 +
    SimplexNoise.noise(x*0.1+523.123, z*0.1+6456.123) * 1
    /*
    math.sin(x * 1.0 / 5.23) * 0.8543 +
    math.sin(z * 1.0 / 5.123) * 0.634 +
    math.sin(z * 1.0 / 84.23) * 30 +
    math.sin(x * 1.0 / 103.34) * 40 +
    math.sin(z * 1.0 / 1084.23) * 150 +
    math.sin(x * 1.0 / 1403.34) * 100
    */
  }



}