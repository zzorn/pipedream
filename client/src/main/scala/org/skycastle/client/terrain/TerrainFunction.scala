package org.skycastle.client.terrain

import com.jme3.math.{Vector3f, Vector2f}


/**
 * 
 */
trait TerrainFunction {

  def getHeight(x: Double, z: Double): Double

  def getHeight(xz: Vector2f): Double = getHeight(xz.x, xz.y)

}