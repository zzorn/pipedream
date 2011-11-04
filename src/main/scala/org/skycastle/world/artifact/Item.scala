package org.skycastle.world.artifact

import org.skycastle.world.Entity
import org.skycastle.world.shape.Shape
import org.skycastle.world.material.{Iron, Material}

/**
 * 
 */
trait Item extends Entity {

  // TODO: Adds an action that can be done with this type of artifact.
  def action(params: AnyRef * ): String = ""

  def material: Material
  def shape: Shape


  val mass = p('mass, 1)

  
}