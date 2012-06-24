package org.skycastle.client.terrain.definition

import java.util.ArrayList

/**
 * 
 */

trait Layer extends Comparable[Layer] {

  /**
   * How deep the layer is compared to other layers.  0 = surface, 1 = bedrock, < 0 = snow etc, > 1 lava etc.
   */
  def order: Double

  /**
   * Calculates layer effect at the specified location and sample scale.
   * Can add zero or more layers to layerMaterialsOut along with corresponding thicknesses to layerThicknessesOut.
   * Can also modify underlying layer thicknesses, or even remove them.
   * @return the change in height at this point. (e.g. if the layer removed 10 meters from the underlying layer and added 7 meters in a new layer, it would return -3.
   */
  def calculate(x: Double,
                z: Double,
                minScale: Double,
                layerMaterialsOut: ArrayList[GroundMaterial],
                layerThicknessesOut: ArrayList[Double]): Double

  def compareTo(other: Layer) = if (order < other.order) -1 else if (order > other.order) 1 else 0
}