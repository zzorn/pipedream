package org.skycastle.parser.model.expressions

/**
 *
 */
case class StringExpr(s: String) extends ConstantValue(s, classOf[String], (s: String) => "\"" + s + "\"")