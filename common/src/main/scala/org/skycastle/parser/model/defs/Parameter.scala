package org.skycastle.parser.model.defs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.{Callable, SyntaxNode, Outputable, TypeDef}

/**
 *
 */
case class Parameter(name: Symbol, initialTypeDef: TypeDef, defaultValue: Option[Expr]) extends Def {

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
    if (initialTypeDef != null) initialTypeDef
    else if (defaultValue.isDefined) defaultValue.get.valueType(visitedNodes)
    else null
  }

  def getMember(name: Symbol) = None

  override def subNodes = singleIt(valueType) ++ defaultValue.iterator

}
