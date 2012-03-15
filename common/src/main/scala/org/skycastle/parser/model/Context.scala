package org.skycastle.parser.model

import defs.{Def, ValDef, FunDef}


/**
 * Keeps track of defined references in a context.
 */
class Context(parentContext: Context = null) {

  private var definitions: Map[Symbol, Def] = Map()

  def addDefinition(definition: Def) {
    definitions += (definition.name -> definition)
  }

  def getDefinitionFor(name: Symbol): Def = {
    definitions.getOrElse(name, {if (parentContext != null) parentContext.getDefinitionFor(name) else null})
  }
  
  def subContext(): Context = {
    new Context(this)
  }


}

