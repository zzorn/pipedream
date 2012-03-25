package org.skycastle.parser.model

import defs.Parameter
import expressions.Expr
import org.skycastle.parser.Context

/**
 *
 */
trait Callable extends ReturnTyped {

  def expression: Expr

  def parameters: List[Parameter]

  lazy val parametersByName: Map[Symbol, Parameter] = parameters.map(p => p.name -> p).toMap
  def parameterByName(name: Symbol): Option[Parameter] = parametersByName.get(name)
  def parameterByIndex(index: Int): Option[Parameter] = if (index < 0 || index >= parameters.size) None else Some(parameters(index))



}