package org.skycastle.parser.model

/**
 *
 */
case class StringExpr(s: String) extends ConstantValue(s, classOf[String], (s: String)  => "\"" + s + "\"")