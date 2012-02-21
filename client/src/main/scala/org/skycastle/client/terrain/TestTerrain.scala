package org.skycastle.client.terrain

import com.jme3.math.{Vector3f, Vector2f}
import org.skycastle.utils.SimplexNoise


/**
 * 
 */
class TestTerrain extends Terrain {

  val up = new Vector3f(0, 1, 0)

  def getHeight(x: Double, z: Double, sampleSize: Double): Double = {

    def mountains(density: Double, size: Double, sharpness: Double, offset: Double): Double = {
      val h = math.min(
        SimplexNoise.noise(
          x * density * offset * 1.234 + 341.123 * offset,
          z * density * offset * 0.9123 + 23.1231),
        SimplexNoise.noise(
          x * density * offset  * 1.1234 + 567.123 * offset,
          z * density * offset  * 0.98213 + 424.234))
      math.abs(math.pow(h, sharpness) * size)
    }


    mountains(0.01,      10, 2, 0.87231) +
    mountains(0.001,     100, 2, 0.912351) +
    mountains(0.0001,    1000, 3, 1.3312314) +
    mountains(0.00001,   15000, 3, 1.1232) +
    mountains(0.0000001, 200000, 5, 0.81231) +
    math.min(math.abs(SimplexNoise.noise(x*0.00013, z*0.000064) * 200),
    SimplexNoise.noise(x*0.0001+63554.12, z*0.0001+5672.1) * 100) +
    SimplexNoise.noise(x*0.002+3454.123, z*0.001+414.123) * 2 *
    SimplexNoise.noise(x*0.041+523.123, z*0.031+6456.123) * 1
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