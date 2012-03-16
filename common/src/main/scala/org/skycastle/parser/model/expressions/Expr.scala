package org.skycastle.parser.model.expressions

import org.skycastle.parser.model.{SyntaxNode, Context, TypeDef, Outputable}


/**
 *
 */
trait Expr extends SyntaxNode {

  def resultType: TypeDef

  def calculation: Context => Any

  def calculate(context: Context): Any = {
    calculation(context)
  }

  def mathType(other: Expr): Class[_] = {
    null
  }


}