package org.skycastle.world.artifact

import org.skycastle.world.material.{Iron, Material}
import org.skycastle.world.shape.{CombinedShape, HammerShape, HandleShape, Shape}
import org.skycastle.world.crafting.{ProjectStatus, Work}

/**
 * For all kinds of hammers etc.
 */
class Hammer extends Item {

  val material: Material = Iron
  val headShape: Shape = new HammerShape()
  val handleShape: Shape = new HandleShape()

  val bash  = action() // An attack
  val forge = action() // Target an item that is under construction and needs forging to progress it forward.  Takes some time, and requires suitable other tools, such as forge
  val nail  = action() //




  def getShape = CombinedShape(headShape, handleShape)


}