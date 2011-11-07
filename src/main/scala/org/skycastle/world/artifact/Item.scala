package org.skycastle.world.artifact

import org.skycastle.world.Entity
import org.skycastle.world.shape.Shape
import org.skycastle.world.material.{Iron, Material}
import org.skycastle.world.crafting.Project

/**
 * 
 */
trait Item extends Entity with Project {

  // TODO: Adds an action that can be done with this type of artifact.
  def action(params: AnyRef * ): String = ""

  def material: Material
  def getShape: Shape

  val wear = p('wear, 0.0) // Amount of wear and tear, at 1 the item breaks.

  val mass = p('mass, 1)

  def getMass = mass()
}