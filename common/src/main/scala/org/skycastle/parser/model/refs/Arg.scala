package org.skycastle.parser.model.refs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.{Indenter, SyntaxNode, Outputable}

/**
 *
 */
case class Arg(value: Expr, paramName: Option[Symbol] = None) extends SyntaxNode {

  override def subNodes = singleIt(value)

  def generateJavaCode(s: StringBuilder, indent: Indenter) {
    value.generateJavaCode(s, indent)
  }
}