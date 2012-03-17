package org.skycastle.parser.model.expressions.math

import org.skycastle.parser.model.expressions.{Expr, ConstantValue}
import org.skycastle.parser.model.{SyntaxNode, NumType}


/**
 *
 */
final case class NumExpr(v: Double) extends Expr {
  def output(s: StringBuilder, indent: Int) {
    s.append(v)
  }

  protected def determineValueType(visitedNodes: Set[SyntaxNode]) = NumType
}