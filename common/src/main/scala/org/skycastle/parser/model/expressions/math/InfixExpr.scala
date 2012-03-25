package org.skycastle.parser.model.expressions.math

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.{SyntaxNode, Indenter}

/**
 *
 */
class InfixExpr extends Expr {

  protected def determineValueType(visitedNodes: Set[SyntaxNode]) = null


  def generateJavaCode(s: StringBuilder, indent: Indenter) = null

}