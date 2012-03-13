package org.skycastle.parser.model.refs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.{Context}

/**
 *
 */
case class Call(function: Symbol, arguments: List[Arg]) extends Expr {

  def resultType = null

  def calculation: Context => Any = {
    null
  }

  def output(s: StringBuilder, indent: Int) {
    s.append(function.name)

    s.append("(")
    if (!arguments.isEmpty) {
      outputSeparatedList(arguments, s, indent + 1)
    }

    s.append(")")
  }
}

