package org.skycastle.parser.model.expressions

import math.Num
import org.skycastle.parser.ResolverContext
import org.skycastle.parser.model._


/**
 *
 */
trait Expr extends ValueTyped {

  // TODO: Implement runtime logic..
  def calculate(context: Context): Expr = Num(42)
}