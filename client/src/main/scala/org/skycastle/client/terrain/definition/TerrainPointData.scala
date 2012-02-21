package org.skycastle.client.terrain.definition

import java.util.ArrayList
import com.jme3.math.ColorRGBA

/**
 * 
 */
final case class TerrainPointData() {

  val maxNumberOfMaterials = 8

  private val groundMaterials = new Array[GroundMaterial](8)

  for (i <- 0 until maxNumberOfMaterials) {
    groundMaterials(i) = new GroundMaterial()
  }




}

final class GroundMaterial() {
  var height: Double = 0
  var strength: Double = 1
  var texture: Symbol = null
  var colorAdjust: ColorRGBA  = ColorRGBA.White
  var stretchX: Double = 1
  var stretchZ: Double = 1
  var scaleY: Double = 1
}
