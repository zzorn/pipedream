package org.skycastle.parser.model.expressions

import org.skycastle.parser.model.SimpleType

/**
 *
 */
case class ConstantValue[T <: Any](value: T, kind: Class[_], output: T => String) extends Expr {

  def resultType = SimpleType(kind)

  def calculation = context => value

  def output(s: StringBuilder, indent: Int) {
    s.append(output(value))
  }
}