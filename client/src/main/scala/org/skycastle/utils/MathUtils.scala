package org.skycastle.utils

import javax.vecmath.Vector3d

/**
 *
 */
object MathUtils {

  def distance(a: Vector3d, b: Vector3d): Double = {
    val dx: Double = a.x - b.x
    val dy: Double = a.y - b.y
    val dz: Double = a.z - b.z
    math.sqrt(dx*dx + dy*dy + dz*dz)
  }

  def distanceSquared(a: Vector3d, b: Vector3d): Double = {
    val dx: Double = a.x - b.x
    val dy: Double = a.y - b.y
    val dz: Double = a.z - b.z
    dx*dx + dy*dy + dz*dz
  }

  def distance(a: Vector3d, x: Double,  y: Double,  z: Double): Double = {
    val dx: Double = a.x - x
    val dy: Double = a.y - y
    val dz: Double = a.z - z
    math.sqrt(dx*dx + dy*dy + dz*dz)
  }

  def distanceSquared(a: Vector3d, x: Double,  y: Double,  z: Double): Double = {
    val dx: Double = a.x - x
    val dy: Double = a.y - y
    val dz: Double = a.z - z
    dx*dx + dy*dy + dz*dz
  }


}