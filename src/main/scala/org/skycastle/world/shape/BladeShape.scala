package org.skycastle.world.shape

/**
 * 
 */
class BladeShape extends Shape {

  val length = p('length, 1.0)
  val width = p('width, 0.1)
  val curvature = p('curvature, 0.0) // 1 = full circle back, 0 = no curvature, -1 full circle forward
  val undulation = p('undulation, 0.0)

}