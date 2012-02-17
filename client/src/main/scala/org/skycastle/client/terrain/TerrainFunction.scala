package org.skycastle.client.terrain

import com.jme3.math.{Vector3f, Vector2f}


/**
 * 
 */
trait TerrainFunction {

  def getHeight(xz: Vector2f): Float

  def getNormal(xz: Vector2f): Vector3f

  def setHeight(xzCoordinate: Vector2f, height: Float)

  def setHeights(xz: java.util.List[Vector2f], height: java.util.List[java.lang.Float]) {
    require(xz.size()== height.size())

    var i = 0
    while (i < xz.size()) {
      setHeight(xz.get(i), height.get(i))
      i += 1
    }
  }

  def adjustHeight(xzCoordinate: Vector2f, delta: Float) {
    setHeight(xzCoordinate, getHeight(xzCoordinate) + delta)
  }

  def adjustHeights(xz: java.util.List[Vector2f], height: java.util.List[java.lang.Float]) {
    require(xz.size() == height.size)

    var i = 0
    while (i < xz.size) {
      adjustHeight(xz.get(i), height.get(i))
      i += 1
    }
  }


}