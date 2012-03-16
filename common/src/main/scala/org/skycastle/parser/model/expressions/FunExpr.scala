package org.skycastle.parser.model.expressions

import org.skycastle.parser.model.TypeDef
import org.skycastle.parser.model.defs.{Def, Parameter}

/**
 *
 */

case class FunExpr(parameters: List[Parameter],
                   resultTypeDef: TypeDef,
                   expression: Expr) extends Expr {

  private val definitionsByName: Map[Symbol, Def] = parameters.map(d => d.name -> d).toMap

  def output(s: StringBuilder, indent: Int) {
    s.append("(")
    outputSeparatedList(parameters, s, indent + 1)
    s.append(")")

    if (resultTypeDef != null) {
      s.append(": ")
      resultTypeDef.output(s, indent)
    }

    s.append(" => ")

    expression.output(s, indent + 1)
  }

  def resultType = resultTypeDef

  def calculation = null

  override def hasContext = true
  override def getContextNamedDef(name: Symbol): Option[Def] = definitionsByName.get(name)


  override def subNodes = parameters.iterator ++ singleIt(resultTypeDef) ++ singleIt(expression)

}
