package org.skycastle.parser.model.defs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model._

/**
 *
 */
case class Parameter(name: Symbol, declaredReturnType: Option[TypeDef], defaultValue: Option[Expr]) extends ValueTyped with ReturnTyped {

  val parameterNamePrefix = "PARAM_"
  
  protected def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef = {
    if (declaredReturnType.isDefined) declaredReturnType.get
    else if (defaultValue.isDefined) defaultValue.get.valueType(visitedNodes)
    else null
  }

  def parameterJavaName: String = parameterNamePrefix + name.name
  
  override def subNodes = declaredReturnType.iterator ++ defaultValue.iterator

  def generateJavaCode(s: StringBuilder, indent: Indenter) {
    s.append(valueType.javaType).append(" ").append(parameterJavaName)
  }
}
