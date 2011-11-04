package org.skycastle.world.artifact

import org.skycastle.world.Entity
import org.skycastle.world.material.{ Material}
import org.skycastle.world.shape.Shape

/**
 * 
 */
trait Item extends Entity {

  // TODO: Adds an action that can be done with this type of artifact.
  def action(params: AnyRef * ): String = ""

  val mass = p('mass, 1)

  
}