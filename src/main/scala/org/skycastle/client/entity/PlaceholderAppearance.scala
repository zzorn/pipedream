package org.skycastle.client.entity

import org.skycastle.client.{UpdatingField, ClientServices}
import com.jme3.scene.{Geometry, Spatial}
import com.jme3.scene.shape.Sphere
import com.jme3.material.Material
import com.jme3.math.ColorRGBA

/**
 * Simple placeholder appearance.
 */
case class PlaceholderAppearance(@UpdatingField var radius: Float = 1,
                                 @UpdatingField var color: ColorRGBA = ColorRGBA.Red) extends Appearance {

  def updateVisualState(props: Map[Symbol, Any]) {}

  protected def createSpatial(services: ClientServices): Spatial = {
    val sphere: Sphere = new Sphere(10, 10, radius)

    val material: Material = new Material(services.engine.getAssetManager, "Common/MatDefs/Misc/Unshaded.j3md")
    material.setColor("color", color)

    val geometry: Geometry = new Geometry("placeholder", sphere)
    geometry.setMaterial(material)
    geometry
  }
}
