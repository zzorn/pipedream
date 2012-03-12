package org.skycastle.parser.model

import defs.FunDef

/**
 *
 */
class Context(parentContext: Context = null) {

  private var definitions: Map[Symbol, FunDef] = Map()
  
  def addDefinition(definition: FunDef) {
    definitions += (definition.name -> definition)
  }

  def getDefinitionFor(name: Symbol): FunDef = {
    definitions.getOrElse(name, {if (parentContext != null) parentContext.getDefinitionFor(name) else null})
  }
  
  def subContext(): Context = {
    new Context(this)
  }


}