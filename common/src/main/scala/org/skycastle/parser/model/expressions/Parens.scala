package org.skycastle.parser.model.expressions

import org.skycastle.parser.model.{Value, MutableContext, SyntaxNode, TypeDef}
import org.skycastle.parser.Context


/**
 *
 */
case class Parens(a: Expr) extends Expr {

  def output(s: StringBuilder, indent: Int) {
    s.append(" (")
    a.output(s, indent)
    s.append(") ")
  }

  override def subNodes = singleIt(a) ++ singleIt(valueType)

  def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef = a.valueType(visitedNodes)

  override def calculate(context: Context): Value = {
    a.calculate(context)
  }

}