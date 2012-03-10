package org.skycastle.parser.model

/**
 *
 */
case class Parens(a: Expression) extends Expression {

  def resultType = a.resultType

  def calculation = a.calculation

  def output(s: StringBuilder, indent: Int) {
    s.append(" (")
    a.output(s, indent)
    s.append(") ")
  }
}