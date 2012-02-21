package org.skycastle.client.terrain.definition

import com.jme3.math.{Vector2f, ColorRGBA}

/**
 * 
 */
case class MaterialLayer(deepness: Double,
                         texture: Symbol,
                         baseColor: ColorRGBA,
                         colorFunction: FunColor,
                         textureScale: Vector2f,
                         hardness: Double,
                         carve: Fun2d,
                         height: Fun2d) extends Layer {

  def calculate(x: Double, z: Double, pointData: TerrainPointData) {
    pointData.carve(x, z)
  }

}

