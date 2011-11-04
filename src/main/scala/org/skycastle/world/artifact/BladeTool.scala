package org.skycastle.world.artifact

/**
 * For swords, daggers, knifes, saws, spears, and axes?
 */
class BladeTool extends CompositeItem {

  val blade: Blade = part(new Blade())
  val handle: Handle = part(new Handle())

  val slash  = action() // Cutting action.  Available if the blade has an edge.
  val thrust = action() // Piercing action.  Available if the blade has a point.
  val bash   = action() // Hacking action.  Available if the blade has a heavy flat.
  val rip    = action() // If there is a sawtooth edge
  val hack   = action() // Axe type action
  val parry  = action() // Block incoming hits

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
