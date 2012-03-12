package org.skycastle.parser.model.expressions

import org.skycastle.parser.model.defs.Parameter
import org.skycastle.parser.model.TypeDef

/**
 *
 */

case class FunExpr(parameters: List[Parameter],
                   resultTypeDef: TypeDef,
                   expression: Expr) extends Expr {

  def output(s: StringBuilder, indent: Int) {
    s.append("(")
    outputSeparatedList(parameters, s, indent + 1)
    s.append(")")

    s.append(": ")
    resultTypeDef.output(s, indent)

    s.append(" = ")

    expression.output(s, indent + 1)
  }

  def resultType = null

  def calculation = null
}
