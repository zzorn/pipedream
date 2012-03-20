package org.skycastle.parser.model.expressions

import org.skycastle.parser.model.defs.{Def, Parameter}
import org.skycastle.parser.model._
import org.skycastle.parser.{Context, ResolverContext}

/**
 *
 */

case class FunExpr(parameters: List[Parameter],
                   declaredReturnType: Option[TypeDef],
                   expression: Expr) extends Expr {




  def output(s: StringBuilder, indent: Int) {
    s.append("(")
    outputSeparatedList(parameters, s, indent + 1)
    s.append(")")

    if (returnType != null) {
      s.append(": ")
      returnType.output(s, indent)
    }

    s.append(" => ")

    expression.output(s, indent + 1)
  }


  def getNestedValue(name: Symbol) = None

  def getParameterDefaultValue(parameterName: Symbol) = null

  def calculate(context: Context) = null

  def nameAndSignature = "("+parameters.mkString(", ")+"): " + (if (returnType == null) "[UnknownType]" else returnType.toString)

  override def subNodes = parameters.iterator ++ declaredReturnType.iterator ++ singleIt(expression)

  def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef = {
    if (declaredReturnType.isDefined) {
      FunType(parameters.map(p => p.valueType(visitedNodes)), declaredReturnType.get)
    }
    else {
      val retType = expression.valueType(visitedNodes)
      if (retType == null) null
      else FunType(parameters.map(p => p.valueType(visitedNodes)), retType)
    }
  }

  def invoke(argumentContext: Context) = null

}
