package org.skycastle.parser.model.expressions


/**
 *
 */
final case object NullExpr extends ConstantValue(null, classOf[AnyRef], (x: AnyRef) => "null")