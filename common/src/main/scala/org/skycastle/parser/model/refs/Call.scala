package org.skycastle.parser.model.refs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.{PathRef, Context}
import org.skycastle.parser.model.defs.Def

/**
 *
 */
case class Call(functionRef: PathRef, arguments: List[Arg]) extends Expr {

  var functionDef: Def = null

  def resultType = null

  def calculation: Context => Any = {
    null
  }

  def output(s: StringBuilder, indent: Int) {
    s.append(functionRef)

    s.append("(")
    if (!arguments.isEmpty) {
      outputSeparatedList(arguments, s, indent + 1)
    }

    s.append(")")
  }

  override def subNodes = arguments.iterator ++ singleIt(resultType)

}

