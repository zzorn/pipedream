package org.skycastle.parser.model.expressions

import org.skycastle.parser.model.SimpleValue._
import org.skycastle.parser.model.{SimpleValue, NumType, Value, MutableContext}

/**
 *
 */
case class StringExpr(s: String) extends ConstantExpr(s, classOf[String], (s: String) => "\"" + s + "\"") {

}