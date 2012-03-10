package org.skycastle.parser.model

/**
 *
 */
class Context(parentContext: Context = null) {

  private var definitions: Map[Symbol, Definition] = Map()
  
  def addDefinition(definition: Definition) {
    definitions += (definition.name -> definition)
  }

  def getDefinitionFor(name: Symbol): Definition = {
    definitions.getOrElse(name, {if (parentContext != null) parentContext.getDefinitionFor(name) else null})
  }
  
  def subContext(): Context = {
    new Context(this)
  }


}