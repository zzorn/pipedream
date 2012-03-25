package org.skycastle.parser.model.expressions

import org.skycastle.parser.model._
import org.skycastle.parser.model.SimpleValue._
import org.skycastle.parser.{Context, ResolverContext}

/**
 *
 */
case class ConstantExpr[T <: Any](value: T, kind: Class[_], output: T => String) extends Expr {


  def output(s: StringBuilder, indent: Int) {
    s.append(output(value))
  }

  override def subNodes = singleIt(valueType)

  def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef = ClassType(Symbol(kind.getName), kind)

  override def calculate(context: Context): Value = constantValue

  private lazy val constantValue = SimpleValue(value, valueType)

}