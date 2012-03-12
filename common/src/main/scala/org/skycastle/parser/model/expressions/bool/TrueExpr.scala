package org.skycastle.parser.model.expressions.bool

import org.skycastle.parser.model.expressions.ConstantValue

final case object TrueExpr extends ConstantValue(true, classOf[Boolean], (x: Boolean) => "true")