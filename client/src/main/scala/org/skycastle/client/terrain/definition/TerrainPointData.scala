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

final case class PointData(var strength: Double = 1,
                           var redAdjust: Double = 0,
                           var greenAdjust: Double = 0,
                           var blueAdjust: Double = 0)

final class GroundMaterial() {
  var texture: Symbol = null
  var colorAdjust: ColorRGBA  = ColorRGBA.White
  var strength: Double = 1
  var stretchX: Double = 1
  var stretchZ: Double = 1
  var scaleY: Double = 1
}
