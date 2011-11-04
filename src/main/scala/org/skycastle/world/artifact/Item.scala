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
  def getShape: Shape


  val concrete = p('concrete, false) // Items are usually just designs first, until progress on them is started they are not concrete.
  val completed = p('completed , false) // TODO: Make state enum for lifecycle states
  val broken = p('broken, false) // TODO: Make state enum for lifecycle states
  val progress = p('progress, 0.0) // Construction progress
  val wear = p('wear, 0.0) // Amount of wear and tear, at 1 the item breaks.

  val quality = p('quality, 1.0) // Quality 1.0 is normal quality, 0 is unusable, broken, 2 is two times better at things than a normal one, etc.

  val mass = p('mass, 1)

  
}