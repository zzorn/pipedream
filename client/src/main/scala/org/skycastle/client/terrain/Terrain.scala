package org.skycastle.client.terrain

import com.jme3.math.{Vector3f, Vector2f}
import java.util.HashMap


/**
 * 
 */
trait Terrain {

  def getHeight(x: Double, z: Double): Double = getHeight(x, z, 1.0)

  def getHeight(x: Double, z: Double, sampleSize: Double): Double

  def getTextures(x: Double, z: Double, textureStrengthsOut: HashMap[Symbol, Double])

}