package org.skycastle.client.terrain

import com.jme3.math.{Vector3f, Vector2f}


/**
 * 
 */
trait TerrainFunction {

  def getHeight(x: Double, z: Double): Double
  def getNormal(x: Double, z: Double): Vector3f

  def getHeight(xz: Vector2f): Double = getHeight(xz.x, xz.y)
  def getNormal(xz: Vector2f): Vector3f = getNormal(xz.x, xz.y)

}