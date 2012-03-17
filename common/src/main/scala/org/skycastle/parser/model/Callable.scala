package org.skycastle.parser.model

import defs.Parameter
import expressions.Expr

/**
 *
 */
trait Callable extends ValueTyped {

  private lazy val paramNamesToNames = parameters.map(p => p.name -> p).toMap 

  def nameAndSignature: String

  def parameters: List[Parameter]
  
  def parameterByName(name: Symbol): Option[Parameter] = paramNamesToNames.get(name)
  def parameterByIndex(index: Int): Option[Parameter] = if (index < 0 || index >= parameters.size) None else Some(parameters(index))

  def expression: Expr


}