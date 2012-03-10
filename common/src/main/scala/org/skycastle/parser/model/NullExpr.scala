package org.skycastle.parser.model

/**
 *
 */
final case object NullExpr extends ConstantValue(null, classOf[AnyRef], (x: AnyRef)  => "null")