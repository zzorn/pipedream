package org.skycastle.parser.syntaxtree.runtime


/**
 * Keeps track of bound references in a context.
 */
class MutableDynamicContext(parentContext: DynamicContext = null) extends DynamicContext {

  private var bindings: Map[Symbol, Value] = Map()

  override def getNestedValue(name: Symbol): Option[Value] = bindings.get(name)

  def addBinding(name: Symbol, value: Value) {
    bindings += (name -> value)
  }

  def hasBinding(name: Symbol): Boolean = {
    bindings.contains(name)
  }

  def subContext(): MutableDynamicContext = {
    new MutableDynamicContext(this)
  }


}
