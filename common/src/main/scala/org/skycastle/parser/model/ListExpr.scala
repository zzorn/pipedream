package org.skycastle.parser.model

/**
 *
 */
case class ListExpr(values: List[Expression]) extends Expression {
  def resultType = null

  def calculation = null

  def output(s: StringBuilder, indent: Int) {
    s.append("[")
    outputSeparatedList(values, s, indent)
    s.append("]")
  }
}