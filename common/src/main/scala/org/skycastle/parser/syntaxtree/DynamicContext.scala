package org.skycastle.parser.syntaxtree

import runtime.Value

/**
 *
 */
trait DynamicContext {

  def getBoundValue(path: List[Symbol]): Value

}