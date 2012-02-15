package org.skycastle.world.shape

/**
 * 
 */

case class CombinedShape(shapes: Shape *) extends Shape {

  override def volume = shapes.foldLeft(0.0){(a, s) => s.volume + a}
  
}