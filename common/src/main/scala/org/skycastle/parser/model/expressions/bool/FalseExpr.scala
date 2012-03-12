package org.skycastle.parser.model.expressions.bool

import org.skycastle.parser.model.expressions.ConstantValue

final case object FalseExpr extends ConstantValue(false, classOf[Boolean], (x: Boolean) => "false")