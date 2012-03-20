package org.skycastle.parser.model.expressions.bool

import org.skycastle.parser.model.expressions.{Expr, ConstantExpr}
import org.skycastle.parser.model._


final case object TrueExpr extends Expr {
  def output(s: StringBuilder, indent: Int) {
    s.append("true")
  }

  protected def determineValueType(visitedNodes: Set[SyntaxNode]) = BoolType

  override def calculate(context: MutableContext): Value = SimpleValue(true, BoolType)

}