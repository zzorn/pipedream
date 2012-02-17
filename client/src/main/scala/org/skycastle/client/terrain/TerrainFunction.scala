package org.skycastle.client.terrain

import com.jme3.math.{Vector3f, Vector2f}


/**
 * 
 */
trait TerrainFunction {

  def height(x: Double, z: Double): Double
  def normal(x: Double, z: Double): Vector3f

  def getHeight(xz: Vector2f): Double = height(xz.x, xz.y)
  def getNormal(xz: Vector2f): Vector3f = normal(xz.x, xz.y)

}