package org.skycastle.parser.syntaxtree


/**
 * Represents something that takes parameters.
 */
trait Parametrized {

  def parameters: List[Parameter]


}