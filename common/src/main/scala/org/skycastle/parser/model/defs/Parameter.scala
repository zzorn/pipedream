package org.skycastle.parser.model.defs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.{SyntaxNode, Outputable, TypeDef}

/**
 *
 */
case class Parameter(name: Symbol, typeDef: TypeDef, defaultValue: Option[Expr]) extends Def {

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

  def getMember(name: Symbol) = None

  override def subNodes = singleIt(typeDef) ++ defaultValue.iterator

}
