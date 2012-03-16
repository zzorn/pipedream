package org.skycastle.parser.model.expressions


/**
 *
 */
case class Parens(a: Expr) extends Expr {

  def resultType = a.resultType

  def calculation = a.calculation

  def output(s: StringBuilder, indent: Int) {
    s.append(" (")
    a.output(s, indent)
    s.append(") ")
  }

  override def subNodes = singleIt(a) ++ singleIt(resultType)

}