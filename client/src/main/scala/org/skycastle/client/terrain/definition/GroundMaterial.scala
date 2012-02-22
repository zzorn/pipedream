package org.skycastle.client.terrain.definition

import com.jme3.math.ColorRGBA
import com.jme3.texture.Texture

// TODO: Rename to layer material?
case class GroundMaterial(name: Symbol,
                          texture: Texture,
                          hardness: Double = 1,
                          stretchX: Double = 1,
                          stretchZ: Double = 1,
                          scaleY: Double = 1,
                          colorAdjust: ColorRGBA = ColorRGBA.White) {
}
