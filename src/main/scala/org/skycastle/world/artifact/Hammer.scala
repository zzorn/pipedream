package org.skycastle.world.artifact

import org.skycastle.world.material.{Iron, Material}
import org.skycastle.world.shape.{CombinedShape, HammerShape, HandleShape, Shape}

/**
 * For all kinds of hammers etc.
 */
class Hammer extends Item {

  val material: Material = Iron
  val headShape: Shape = new HammerShape()
  val handleShape: Shape = new HandleShape()

  val bash  = action() // An attack
  val forge = action() //
  val nail  = action() //


  def shape = CombinedShape(headShape, handleShape)


}