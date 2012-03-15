package org.skycastle.parser.model.defs

import org.skycastle.parser.model._
import expressions.Expr


/**
 *
 */
case class FunDef(name: Symbol,
                  parameters: List[Parameter],
                  resultTypeDef: TypeDef,
                  expression: Expr) extends Def with Outputable {

  def output(s: StringBuilder, indent: Int) {
    createIndent(s, indent)
    s.append("fun ")
      .append(name.name)
      .append("(")

    outputSeparatedList(parameters, s, indent + 1)
    s.append(")")

    if (resultTypeDef != null) {
      s.append(": ")
      resultTypeDef.output(s, indent)
    }
    s.append(" = ")

    expression.output(s, indent + 1)
  }


  def getMember(name: Symbol) = None

  lazy val typeDef: TypeDef = FunType(parameters.map( _.typeDef), resultTypeDef)
}