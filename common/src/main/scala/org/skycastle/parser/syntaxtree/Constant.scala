package org.skycastle.parser.syntaxtree

import java.util.ArrayList
import org.skycastle.parser.model.{ClassType, TypeDef}
import runtime.{DynamicContext, Value}

/**
 *
 */
class Constant(value: Value, kind: TypeDef) extends Expr {

  def output(s: StringBuilder, indent: Int) {
    s.append(value)
  }

  protected def determineValueType(visitedNodes: Set[AstNode]) = Some(kind)

  def calculate(dynamicContext: DynamicContext) = value
}