package org.skycastle.client.terrain.definition

import com.jme3.math.ColorRGBA

/**
 * 2D function, for terrain etc.
 */
trait Fun2d {

  def apply(x: Double, z: Double): Double = apply(x, z, 0, Double.PositiveInfinity)
  def apply(x: Double, z: Double, minScale: Double): Double = apply(x, z, minScale, Double.PositiveInfinity)
  def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double

}

/**
 * Color function, for terrain etc.
 */
trait FunColor {

  def apply(x: Double, z: Double): ColorRGBA = apply(x, z, 0, Double.PositiveInfinity)
  def apply(x: Double, z: Double, minScale: Double): ColorRGBA = apply(x, z, minScale, Double.PositiveInfinity)
  def apply(x: Double, z: Double, minScale: Double, maxScale: Double): ColorRGBA

}

