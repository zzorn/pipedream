package org.skycastle.parser.model.refs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.{Context}

/**
 *
 */
case class Call(function: Symbol, unnamedArguments: List[Expr], namedArguments: List[NamedArg]) extends Expr {

  def resultType = null

  def calculation: Context => Any = {
    null
  }

  def output(s: StringBuilder, indent: Int) {
    s.append(function.name)

    s.append("(")
    if (!unnamedArguments.isEmpty) {
      outputSeparatedList(unnamedArguments, s, indent + 1)
    }

    if (!unnamedArguments.isEmpty && !namedArguments.isEmpty) s.append(", ")

    if (!namedArguments.isEmpty) {
      outputSeparatedList(namedArguments, s, indent + 1)
    }
    s.append(")")
  }
}

