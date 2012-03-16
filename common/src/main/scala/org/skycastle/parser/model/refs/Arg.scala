package org.skycastle.parser.model.refs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.{SyntaxNode, Outputable}

/**
 *
 */
case class Arg(paramName: Option[Symbol], value: Expr) extends SyntaxNode {

  def output(s: StringBuilder, indent: Int) {
    if (paramName.isDefined) {
      s.append(paramName.get.name)
      s.append(" = ")
    }
    value.output(s, indent)
  }

  override def subNodes = singleIt(value)

}