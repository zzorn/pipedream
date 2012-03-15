package org.skycastle.parser.model.module

import org.skycastle.parser.model.defs.Def
import org.skycastle.parser.model.{Outputable}


/**
 *
 */
final case class Module(name: Symbol, imports: List[Import], var definitions: List[Def]) extends Def with Outputable {
  
  private var defsByName: Map[Symbol, Def] = Map()
  
  definitions foreach {d => defsByName += d.name -> d}

  def addDefinition(definition: Def) {
    if (defsByName.contains(definition.name)) {
      throw new IllegalArgumentException("Module '"+name+"' already contains a definition with the name '"+definition.name+"'")
    }

    defsByName += definition.name -> definition
    definitions ::= definition
  }

  def output(s: StringBuilder, indent: Int) {
    createIndent(s, indent)
    s.append("module ").append(name.name).append(" {\n")
    outputTerminatedList(imports, s, indent + 1, "\n")
    outputTerminatedList(definitions, s, indent + 1, "\n")
    createIndent(s, indent)
    s.append("}\n")
  }

  override def getMember(name: Symbol): Option[Def] = defsByName.get(name)

  def typeDef = null
}