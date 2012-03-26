package org.skycastle.parser.model.expressions

import org.skycastle.parser.model.expressions.{Expr, ConstantExpr}
import org.skycastle.parser.model.SimpleValue._
import org.skycastle.parser.model._


/**
 *
 */
final case class Num(v: Double) extends Expr {
  def output(s: StringBuilder, indent: Int) {
    s.append(v)
  }

  protected def determineValueType(visitedNodes: Set[SyntaxNode]) = NumType

  def generateJavaCode(s: StringBuilder, indent: Indenter) {
    s.append(v)
  }
}