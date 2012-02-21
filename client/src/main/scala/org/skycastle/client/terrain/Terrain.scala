package org.skycastle.client.terrain

import com.jme3.math.{Vector3f, Vector2f}
import definition.{PointData, GroundMaterial}
import java.util.{ArrayList, HashMap}


/**
 * 
 */
trait Terrain {

  def getHeight(x: Double, z: Double): Double = getHeight(x, z, 1.0)

  def getHeight(x: Double, z: Double, sampleSize: Double): Double

  def getHeightAndTextures(x: Double, z: Double, textureData: HashMap[Symbol, PointData]): Double

}