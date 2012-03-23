package org.skycastle.parser.syntaxtree

import java.util.ArrayList
import org.skycastle.parser.model.{SimpleType, TypeDef}

/**
 *
 */
class Constant(value: Any, kind: SimpleType) extends Expr {

  def output(s: StringBuilder, indent: Int) {
    s.append(value)
  }

  protected def determineValueType(visitedNodes: Set[AstNode]) = kind
}