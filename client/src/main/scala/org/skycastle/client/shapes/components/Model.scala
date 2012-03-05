package org.skycastle.client.shapes.components

import com.jme3.material.Material
import com.jme3.app.Application._
import com.jme3.math.ColorRGBA
import com.jme3.asset.AssetManager
import com.jme3.scene.{Mesh, Geometry, Spatial}


/**
 *
 */

trait Model {


  def createSpatial(assetManager: AssetManager): Spatial = {
    val mesh = createMesh()
    val geometry: Geometry = new Geometry("shape", mesh)
    geometry.setMaterial(createMaterial(assetManager))
    geometry
  }

  def createMesh(): Mesh

  def createMaterial(assetManager: AssetManager): Material = {
    val mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", ColorRGBA.randomColor());
    mat
  }

}
