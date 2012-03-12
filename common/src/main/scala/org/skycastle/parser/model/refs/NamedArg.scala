package org.skycastle.parser.model.refs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.{Outputable}

/**
 *
 */
case class NamedArg(paramName: Symbol, value: Expr) extends Outputable {

  def output(s: StringBuilder, indent: Int) {
    s.append(paramName.name)
    s.append(" = ")
    value.output(s, indent)
  }

}