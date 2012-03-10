package org.skycastle.parser.model

/**
 *
 */
trait Expression extends Outputable {

  def resultType: Class[_]

  def calculation: Context => Any

  def calculate(context: Context): Any = {
    calculation(context)
  }

  def mathType(other: Expression): Class[_] = {
    null
  }


}