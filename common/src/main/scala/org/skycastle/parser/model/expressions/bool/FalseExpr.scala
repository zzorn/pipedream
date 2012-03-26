package org.skycastle.parser.model.expressions.bool

import org.skycastle.parser.model.expressions.{Expr, ConstantExpr}
import org.skycastle.parser.model.SimpleValue._
import org.skycastle.parser.model._

case object FalseExpr extends Expr {

  protected def determineValueType(visitedNodes: Set[SyntaxNode]) = BoolType

  def generateJavaCode(s: StringBuilder, indent: Indenter) {
    s.append("false")
  }
}