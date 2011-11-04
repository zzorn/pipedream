package org.skycastle.world.artifact

import org.skycastle.world.material.{Iron, Material}

/**
 * For swords, daggers, knifes
 */
class Blade extends Item {

  val material: Material = Iron

  val length = p('length, 0.8)
  val width = p('width, 0.1)
  val curvature = p('curvature, 0.3) // 1 = full circle back, 0 = no curvature, -1 full circle forward

  val slash  = action() // Cutting action.  Available if the blade has an edge.
  val thrust = action() // Piercing action.  Available if the blade has a point.
  val parry  = action() // Block incoming hits

  def getMass = mass()

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
