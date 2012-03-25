package org.skycastle.parser.syntaxtree

import java.util.ArrayList
import org.skycastle.parser.model.SyntaxError

/**
 *
 */
case class Argument(valueExpr: Expr, paramName: Option[Symbol] = None) extends AstNode {


  def childNodes = singleIt(valueExpr)

  def output(s: StringBuilder, indent: Int) {
    if (paramName.isDefined) {
      s.append(paramName.get.name).append(" = ")
    }
    valueExpr.output(s, indent)
  }

  def checkForErrors(errors: ArrayList[SyntaxError]) {
    super.checkForErrors(errors)

    // TODO
  }
}