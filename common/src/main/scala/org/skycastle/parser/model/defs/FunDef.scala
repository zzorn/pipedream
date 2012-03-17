package org.skycastle.parser.model.defs

import org.skycastle.parser.model._
import expressions.Expr


/**
 *
 */
case class FunDef(name: Symbol,
                  parameters: List[Parameter],
                  initialResultTypeDef: Option[TypeDef],
                  expression: Expr) extends Def with Callable {

  private val paramsByName: Map[Symbol, Parameter] = parameters.map(p => p.name -> p).toMap

  def output(s: StringBuilder, indent: Int) {
    createIndent(s, indent)
    s.append("fun ")
      .append(name.name)
      .append("(")

    outputSeparatedList(parameters, s, indent + 1)
    s.append(")")

    if (returnType != null) {
      s.append(": ")
      returnType.output(s, indent)
    }
    s.append(" = ")

    expression.output(s, indent + 1)
  }

  override def subNodes = parameters.iterator ++ initialResultTypeDef.iterator ++ singleIt(expression)

  def getMember(name: Symbol) = None

  override def hasContext = true
  override def getContextNamedDef(name: Symbol): Option[Def] = paramsByName.get(name)

  def nameAndSignature = name.name + "("+parameters.mkString(", ")+"): " + (if (returnType == null) "[UnknownType]" else returnType.toString)

  protected def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef = {
    if (initialResultTypeDef.isDefined) {
      FunType(parameters.map(p => p.valueType(visitedNodes)), initialResultTypeDef.get)
    }
    else {
      val retType = expression.valueType(visitedNodes)
      if (retType == null) null
      else FunType(parameters.map(p => p.valueType(visitedNodes)), retType)
    }
  }

}