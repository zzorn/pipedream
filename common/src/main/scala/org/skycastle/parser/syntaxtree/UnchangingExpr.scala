package org.skycastle.parser.syntaxtree

import runtime.DynamicContext
import org.skycastle.parser.model.Value

/**
 *
 */
trait UnchangingExpr extends Expr {
  
  private lazy val calculatedValue = calculate()
  
  final def calculate(dynamicContext: DynamicContext): Value = calculatedValue
  
  protected def calculate(): Value
}