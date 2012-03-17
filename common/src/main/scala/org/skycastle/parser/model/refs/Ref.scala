package org.skycastle.parser.model.refs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.defs.Def
import org.skycastle.parser.ResolverContext
import org.skycastle.parser.model.{SyntaxNode, TypeDef, PathRef}

/**
 *
 */

case class Ref(path: PathRef) extends Expr {

  var definition: Def = null

  def output(s: StringBuilder, indent: Int) {
    s.append(path)
  }

  override def subNodes = singleIt(valueType)

  def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef =
    if (definition != null) definition.valueType(visitedNodes) else null

}
