package org.skycastle.parser.model

import defs.{Def, ValDef, FunDef}
import expressions.Expr
import org.skycastle.parser.Context


/**
 * Keeps track of bound references in a context.
 */
class MutableContext(parentContext: Context = null) extends Context {

  private var bindings: Map[Symbol, Value] = Map()

  override def getNestedValue(name: Symbol): Option[Value] = bindings.get(name)

  def addBinding(name: Symbol, value: Value) {
    bindings += (name -> value)
  }

  def hasBinding(name: Symbol): Boolean = {
    bindings.contains(name)
  }

  def subContext(): MutableContext = {
    new MutableContext(this)
  }


}

