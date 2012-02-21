package org.skycastle.client.terrain.definition

/**
 * 
 */

trait Layer {

  def deepness: Double

  def calculate(x: Double, z: Double, materialDataOut: GroundMaterial)

}