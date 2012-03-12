package org.skycastle.parser.model.expressions

/**
 *
 */
case class ListExpr(values: List[Expr]) extends Expr {
  def resultType = null

  def calculation = null

  def output(s: StringBuilder, indent: Int) {
    s.append("[")
    outputSeparatedList(values, s, indent)
    s.append("]")
  }
}