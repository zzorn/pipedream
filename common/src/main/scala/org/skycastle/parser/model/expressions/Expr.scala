package org.skycastle.parser.model.expressions

import org.skycastle.parser.model.{Context, TypeDef, Outputable}

/**
 *
 */
trait Expr extends Outputable {

  def resultType: TypeDef

  def calculation: Context => Any

  def calculate(context: Context): Any = {
    calculation(context)
  }

  def mathType(other: Expr): Class[_] = {
    null
  }


}