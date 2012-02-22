package org.skycastle.client.terrain.definition

import com.jme3.math.{Vector2f, ColorRGBA}
import java.util.ArrayList

/**
 * 
 */
case class MaterialLayer(order: Double,
                         layerMaterial: GroundMaterial,
                         layerThickness: Fun2d) extends Layer {


  def calculate(x: Double,
                z: Double,
                minScale: Double,
                layerMaterialsOut: ArrayList[GroundMaterial],
                layerThicknessesOut: ArrayList[Double]): Double = {

    val thickness = layerThickness(x, z, minScale)
    if (thickness > 0.0) {
      layerMaterialsOut.add(layerMaterial)
      layerThicknessesOut.add(thickness)
    }
    math.max(thickness, 0.0)
  }

}

