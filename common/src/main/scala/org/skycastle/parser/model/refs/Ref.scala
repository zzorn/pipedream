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

  def output(s: StringBuilder, indent: Int) {
    s.append(path)
  }

  override def subNodes = singleIt(valueType)

  def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef =
    if (definition != null) definition.valueType(visitedNodes) else null

  def calculate(context: Context): Value = {
    context.getValue(path.path) match {
      case Some(v) => v
      case None => throw new RunError("Could not find reference '"+path+"'", this)
    }
  }
}
