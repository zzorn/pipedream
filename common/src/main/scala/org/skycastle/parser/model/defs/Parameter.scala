package org.skycastle.parser.model.defs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.{Outputable, TypeDef}

/**
 *
 */
case class Parameter(name: Symbol, typeDef: TypeDef, defaultValue: Option[Expr]) extends Outputable {

  def output(s: StringBuilder, indent: Int) {
    s.append(name.name)

    if (typeDef != null) {
      s.append(": ")
      typeDef.output(s, indent)
    }

    if (defaultValue.isDefined) {
      s.append(" = ")
      defaultValue.get.output(s, indent)
    }
  }
}
