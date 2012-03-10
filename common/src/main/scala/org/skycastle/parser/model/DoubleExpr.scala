package org.skycastle.parser.model

/**
 *
 */
final case class DoubleExpr(v: Double) extends ConstantValue(v, classOf[Double], (d: Double) => d.toString)