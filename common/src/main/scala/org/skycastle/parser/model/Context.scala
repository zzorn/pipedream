package org.skycastle.parser.model

import defs.{Def, ValDef, FunDef}
import expressions.Expr


/**
 * Keeps track of bound references in a context.
 */
class Context(parentContext: Context = null) {


  private var bindings: Map[Symbol, Expr] = Map()

  def addBinding(name: Symbol, value: Expr) {
    bindings += (name -> value)
  }

  def getBindingFor(name: Symbol): Expr = {
    bindings.getOrElse(name, {if (parentContext != null) parentContext.getBindingFor(name) else null})
  }

  def hasBinding(name: Symbol): Boolean = {
    bindings.contains(name)
  }

  def subContext(): Context = {
    new Context(this)
  }


}

