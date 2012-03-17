package org.skycastle.parser.model.defs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model._

/**
 *
 */
case class Parameter(name: Symbol, declaredReturnType: Option[TypeDef], defaultValue: Option[Expr]) extends Def  with ValueTyped with ReturnTyped {

  def output(s: StringBuilder, indent: Int) {
    s.append(name.name)

    if (valueType != null) {
      s.append(": ")
      valueType.output(s, indent)
    }

    if (defaultValue.isDefined) {
      s.append(" = ")
      defaultValue.get.output(s, indent)
    }
  }

  protected def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef = {
    if (declaredReturnType.isDefined) declaredReturnType.get
    else if (defaultValue.isDefined) defaultValue.get.valueType(visitedNodes)
    else null
  }

  def getMember(name: Symbol) = None

  override def subNodes = singleIt(valueType) ++ declaredReturnType.iterator ++ defaultValue.iterator

}
