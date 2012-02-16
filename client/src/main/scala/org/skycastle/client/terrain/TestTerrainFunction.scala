package org.skycastle.client.terrain

import com.jme3.math.{Vector3f, Vector2f}


/**
 * 
 */
class TestTerrainFunction extends TerrainFunction {

  val up = new Vector3f(0, 1, 0)

  def getHeight(xz: Vector2f): Float = {
    (math.cos(xz.x * 0.001) * 100 +
     math.sin(xz.y * 0.02) * 70).toFloat
  }

  def getNormal(xz: Vector2f) = up

  def setHeight(xzCoordinate: Vector2f, height: Float) {
    throw new UnsupportedOperationException("Set height not supported")
  }

}