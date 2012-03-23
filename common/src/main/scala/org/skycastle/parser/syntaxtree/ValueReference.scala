package org.skycastle.parser.syntaxtree

import java.util.ArrayList
import org.skycastle.parser.model.TypeDef

/**
 *
 */
case class ValueReference(path: List[Symbol]) extends Reference with Expr {


  def output(s: StringBuilder, indent: Int) {

  }

  protected def determineValueType(visitedNodes: Set[AstNode]): Option[TypeDef] = {
    referencedNode match {
      case Some(e: Expr) => e.valueType
      case _ => None
    }
  }
}