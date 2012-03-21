package org.skycastle.parser.model.expressions

import org.skycastle.parser.model._
import org.skycastle.parser.{Context, ResolverContext}

/**
 *
 */
case class ListExpr(values: List[Expr]) extends Expr {

  def output(s: StringBuilder, indent: Int) {
    s.append("[")
    outputSeparatedList(values, s, indent)
    s.append("]")
  }

  override def subNodes = values.iterator ++ singleIt(valueType)

  def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef = {
    val t = values.foldLeft[TypeDef](NothingType) {(t, e) => if (t == null) null else t.mostSpecificCommonType(e.valueType(visitedNodes))}
    if (t == null) null
    else ListType(t)
  }

  def calculate(context: Context): Value = {
    val list = values map {e => e.calculate(context).unwrappedValue}
    SimpleValue(list, valueType)
  }
}

