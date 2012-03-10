package org.skycastle.parser.model

/**
 *
 */
case class ConstantValue[T <: Any](value: T, kind: Class[T], output: T => String) extends Expression {

  def resultType = kind

  def calculation = context => value

  def output(s: StringBuilder, indent: Int) {
    s.append(output(value))
  }
}