package org.skycastle.world.shape

/**
 * 
 */
class HandleShape extends Shape {

  val radius = p('radius, 0.03)
  val length = p('length, 0.3)

  override def volume: Double = length() * math.Pi * radius() * radius()

}