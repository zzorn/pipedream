package org.skycastle.parser.model

final case object FalseExpr extends ConstantValue(false, classOf[Boolean], (x: Boolean)  => "false")