package org.skycastle.client.terrain.definition

import scala.collection.JavaConversions._
import java.util.{ArrayList, Collections}

/**
 *
 */
class GroundDef {

  private val layers = new ArrayList[Layer]()

  def addLayer(layer: Layer) {
    layers.add(layer)
    Collections.sort(layers)
  }

  def removeLayer(layer: Layer) {
    layers.remove(layer)
  }

  /**
   * NOTE: This is expensive, as it creates two new arraylists with every call!
   */
  def getHeight(x: Double, z: Double, minScale: Double): Double = {
    calculate(x, z, minScale, new ArrayList[GroundMaterial](), new ArrayList[Double]())
  }

  def calculate(x: Double, z: Double, minScale: Double,
                layerMaterialsOut: ArrayList[GroundMaterial],
                layerThicknessesOut: ArrayList[Double]): Double = {
    val layerCount = layers.size
    var height = 0.0
    var i = 0
    while (i < layerCount) {
      height += layers(i).calculate(x, z, minScale, layerMaterialsOut, layerThicknessesOut)
      i += 1
    }
    height
  }

}