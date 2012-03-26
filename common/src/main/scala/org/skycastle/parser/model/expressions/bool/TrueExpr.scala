package org.skycastle.parser.model.expressions.bool

import org.skycastle.parser.model.expressions.{Expr, ConstantExpr}
import org.skycastle.parser.model._


case object TrueExpr extends Expr {

  protected def determineValueType(visitedNodes: Set[SyntaxNode]) = BoolType

  def generateJavaCode(s: StringBuilder, indent: Indenter) {
    s.append("true")
  }


}