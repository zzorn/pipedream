package org.skycastle.parser.syntaxtree.runtime

import org.skycastle.parser.syntaxtree.Parametrized

/**
 *
 */
trait Invokable extends Parametrized {

  def invoke(parameterValues: DynamicContext): Value

}