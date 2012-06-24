package org.skycastle.client.terrain.definition

import java.util.ArrayList

/**
 * A layer that has absolute height, which can be negative.
 */
case class FoundationLayer(order: Double,
                           layerMaterial: GroundMaterial,
                           layerHeight: Fun2d) extends Layer {


  def calculate(x: Double,
                z: Double,
                minScale: Double,
                layerMaterialsOut: ArrayList[GroundMaterial],
                layerThicknessesOut: ArrayList[Double]): Double = {

    val height = layerHeight(x, z, minScale)
    layerMaterialsOut.add(layerMaterial)
    layerThicknessesOut.add(height)
    height
  }

}

