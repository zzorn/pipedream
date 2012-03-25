package org.skycastle.parser.model.refs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.defs.Def
import org.skycastle.parser.model._
import org.skycastle.parser.{RunError, Context, ResolverContext}

/**
 *
 */

case class Ref(path: PathRef) extends Expr {

  var definition: ValueTyped = null

  override def subNodes = singleIt(valueType)

  def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef =
    if (definition != null) definition.valueType(visitedNodes) else null


  def generateJavaCode(s: StringBuilder, indent: Indenter) {
    s.append(path)
  }
}
