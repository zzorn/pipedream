package org.skycastle.parser.model.defs

import org.skycastle.parser.model._
import expressions.Expr
import collection.immutable.List
import refs.Arg
import org.skycastle.parser.{Context,  RunError}


/**
 *
 */
case class FunDef(name: Symbol,
                  parameters: List[Parameter],
                  declaredReturnType: Option[TypeDef],
                  expression: Expr) extends Def with Callable {

  private var value: Closure = null

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

  override def subNodes = parameters.iterator ++ declaredReturnType.iterator ++ singleIt(expression)

  def getMember(name: Symbol) = None

  override def hasContext = true
  override def getContextNamedDef(name: Symbol): Option[Def] = parametersByName.get(name)

  def nameAndSignature = name.name + "("+parameters.mkString(", ")+"): " + (if (returnType == null) "[UnknownType]" else returnType.toString)



  def getParameterDefaultValue(parameterName: Symbol): Option[Value] = {
    parametersByName.get(parameterName).flatMap(_.defaultValue).map(_.calculate(this))
  }


  def calculate(context: Context): Closure = {
    if (value == null) value = Closure(context, parameters, expression, valueType)
    value
  }

  protected def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef = {
    if (declaredReturnType.isDefined) {
      FunType(parameters.map(p => p.valueType(visitedNodes)), declaredReturnType.get)
    }
    else {
      val retType = expression.valueType(visitedNodes)
      if (retType == null) null
      else FunType(parameters.map(p => p.valueType(visitedNodes)), retType)
    }
  }

}