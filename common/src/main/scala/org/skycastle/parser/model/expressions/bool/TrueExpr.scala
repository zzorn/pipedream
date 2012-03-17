package org.skycastle.parser.model.expressions.bool

import org.skycastle.parser.model.expressions.{Expr, ConstantValue}
import org.skycastle.parser.model.{BoolType, SyntaxNode}


final case object TrueExpr extends Expr {
  def output(s: StringBuilder, indent: Int) {
    s.append("true")
  }

  protected def determineValueType(visitedNodes: Set[SyntaxNode]) = BoolType
}