package org.skycastle.world.artifact

import org.skycastle.world.material.{Iron, Material}
import org.skycastle.world.shape.{BladeShape, Shape}

/**
 * For swords, daggers, knifes
 */
class Blade extends Item {

  var material: Material = Iron
  val shape = p('shape, new BladeShape())
  val name = p('name, "blade")

  val slash  = action() // Cutting action.  Available if the blade has an edge.
  val thrust = action() // Piercing action.  Available if the blade has a point.
  val parry  = action() // Block incoming hits

  def getMass = mass()
  def getShape = shape()

  // name (TODO: Infer from field name),
  // difficulty,
  // base skills / place in skill hierarchy?,
  // parameters required and their type?
  // code to execute when carried out
  // Animation
  // Code calculates:
  // * Energy usage
  // * Actual animation to use
  // * difficulty
  // * duration
  // * code to do on success %
  // After duration, check success, increase skills, run success code with success %

  




}
