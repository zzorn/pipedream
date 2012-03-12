package org.skycastle.parser.model.expressions.math

import org.skycastle.parser.model.expressions.ConstantValue

/**
 *
 */
final case class DoubleExpr(v: Double) extends ConstantValue(v, classOf[Double], (d: Double) => d.toString)