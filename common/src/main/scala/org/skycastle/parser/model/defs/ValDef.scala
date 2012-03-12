package org.skycastle.parser.model.defs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.{TypeDef}


/**
 *
 */
case class ValDef(name: Symbol,
                  typeDef: TypeDef,
                  value: Expr) extends Def {
  
  def output(s: StringBuilder, indent: Int) {
    s.append("val ")
    s.append(name.name)
    s.append(": ")
    typeDef.output(s, indent)
    s.append(" = ")
    value.output(s, indent)
  }
  
}
