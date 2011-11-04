package org.skycastle.world.shape

/**
 * 
 */
class HammerShape extends Shape {

  val radius = p('width, 0.05)
  val length = p('width, 0.2)

  override def volume: Double = length() * math.Pi * radius() * radius()

}