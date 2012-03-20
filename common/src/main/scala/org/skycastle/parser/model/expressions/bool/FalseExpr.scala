package org.skycastle.parser.model.expressions.bool

import org.skycastle.parser.model.expressions.{Expr, ConstantExpr}
import org.skycastle.parser.model.SimpleValue._
import org.skycastle.parser.model._

final case object FalseExpr extends Expr {
  def output(s: StringBuilder, indent: Int) {
    s.append("false")
  }

  protected def determineValueType(visitedNodes: Set[SyntaxNode]) = BoolType

  override def calculate(context: MutableContext): Value = SimpleValue(false, BoolType)

}