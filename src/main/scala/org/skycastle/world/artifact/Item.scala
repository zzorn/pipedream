package org.skycastle.world.artifact

import org.skycastle.world.Entity
import org.skycastle.world.material.{Placeholderium, Material}
import org.skycastle.world.shape.Shape

/**
 * 
 */
trait Item extends Entity {

  // TODO: Adds an action that can be done with this type of artifact.
  def action(params: AnyRef * ): String = ""

  
}