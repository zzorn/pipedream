package org.skycastle.parser.model.expressions

import org.skycastle.parser.ResolverContext
import org.skycastle.parser.model.{SyntaxNode, TypeDef, SimpleType}

/**
 *
 */
case class ConstantValue[T <: Any](value: T, kind: Class[_], output: T => String) extends Expr {


  def output(s: StringBuilder, indent: Int) {
    s.append(output(value))
  }

  override def subNodes = singleIt(valueType)

  def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef = SimpleType(Symbol(kind.getName), kind)

}