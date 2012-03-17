package org.skycastle.parser.model.expressions


/**
 *@deprecated not needed, hopefully?
 */
final case object NullExpr extends ConstantValue(null, classOf[AnyRef], (x: AnyRef) => "null")