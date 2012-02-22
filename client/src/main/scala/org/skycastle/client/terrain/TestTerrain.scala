package org.skycastle.client.terrain

import com.jme3.math.{Vector3f, Vector2f}
import org.skycastle.utils.SimplexNoise
import java.util.HashMap


/**
 * 
 */
class TestTerrain /*extends Terrain */{

  val up = new Vector3f(0, 1, 0)


  def getTextures(x: Double, z: Double, textureStrengthsOut: HashMap[Symbol, Double]) = null


  def getHeightAndTextures(x: Double, z: Double, textureData: HashMap[Symbol, Double]): Double =  {
    val grass1: Double = math.max(mountains(x, z, 0.007, 20, 2, 0.87231),
                                  mountains(x, z, 0.0051, 25, 2, 0.97231))
    val grass =
      grass1 +
      mountains(x, z, 0.001,     100, 2, 0.912351)

    val sand1: Double = mountains(x, z, 0.0001, 70, 3, 1.3312314) +
                        mountains(x, z, 0.000001, 30, 2, 1.0892481231)
    val sand =
      sand1 +
      mountains(x, z, 0.00001,   15000, 3, 1.1232)

    val stone1: Double = mountains(x, z, 0.0001, 100, 3, 0.92481231) -
                         mountains(x, z, 0.000001,  30, 4, 0.892481231) +
                         mountains(x, z, 0.00000012, 40, 4, 1.04892481231)
    val stone =
      stone1 +
      mountains(x, z, 0.0000001, 200000, 4, 0.81231)

    if (textureData != null) {
      /*
      textureData.get('twisty_grass).strength = grass1
      textureData.get('sand).strength = sand1
      textureData.get('regolith).strength = stone1
      */
    }

    stone + sand + grass
  }

  def getHeight(x: Double, z: Double, sampleSize: Double): Double = {
    getHeightAndTextures(x, z, null)
    /*
    mountains(x, z, 0.01,      10, 2, 0.87231) +
    mountains(x, z, 0.001,     100, 2, 0.912351) +
    mountains(x, z, 0.0001,    1000, 3, 1.3312314) +
    mountains(x, z, 0.00001,   15000, 3, 1.1232) +
    mountains(x, z, 0.0000001, 200000, 5, 0.81231) +
    math.min(math.abs(SimplexNoise.noise(x*0.00013, z*0.000064) * 200),
    SimplexNoise.noise(x*0.0001+63554.12, z*0.0001+5672.1) * 100) +
    SimplexNoise.noise(x*0.002+3454.123, z*0.001+414.123) * 2 *
    SimplexNoise.noise(x*0.041+523.123, z*0.031+6456.123) * 1
    */
    /*
    math.sin(x * 1.0 / 5.23) * 0.8543 +
    math.sin(z * 1.0 / 5.123) * 0.634 +
    math.sin(z * 1.0 / 84.23) * 30 +
    math.sin(x * 1.0 / 103.34) * 40 +
    math.sin(z * 1.0 / 1084.23) * 150 +
    math.sin(x * 1.0 / 1403.34) * 100
    */
  }

  private final def mountains(x: Double, z: Double, density: Double, size: Double, sharpness: Double, offset: Double): Double = {
    val h = math.min(
      SimplexNoise.noise(
        x * density * offset * 1.234 + 341.123 * offset,
        z * density * offset * 0.9123 + 23.1231),
      SimplexNoise.noise(
        x * density * offset  * 1.1234 + 567.123 * offset,
        z * density * offset  * 0.98213 + 424.234))
    math.abs(math.pow(h, sharpness) * size)
  }


}