package org.skycastle.parser.model.expressions

import math.Num
import org.skycastle.parser.model._
import org.skycastle.parser.{Context, ResolverContext}


/**
 *
 */
trait Expr extends ValueTyped {

  def calculate(context: Context): Value
  
}
