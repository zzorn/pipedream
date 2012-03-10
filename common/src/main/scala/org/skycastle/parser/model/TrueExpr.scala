package org.skycastle.parser.model

final case object TrueExpr extends ConstantValue(true, classOf[Boolean], (x: Boolean) => "true")