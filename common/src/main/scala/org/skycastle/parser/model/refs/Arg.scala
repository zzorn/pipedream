package org.skycastle.parser.model.refs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.{Outputable}

/**
 *
 */
case class Arg(paramName: Option[Symbol], value: Expr) extends Outputable {

  def output(s: StringBuilder, indent: Int) {
    if (paramName.isDefined) {
      s.append(paramName.get.name)
      s.append(" = ")
    }
    value.output(s, indent)
  }

}