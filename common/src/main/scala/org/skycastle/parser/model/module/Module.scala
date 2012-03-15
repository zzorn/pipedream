package org.skycastle.parser.model.module

import org.skycastle.parser.model.defs.Def
import org.skycastle.parser.model.{Referable, Outputable}


/**
 *
 */
final case class Module(name: Symbol, imports: List[Import], definitions: List[Def]) extends Outputable with Referable {
  
  private var defsByName: Map[Symbol, Def] = Map()
  
  definitions foreach {d => defsByName += d.name -> d}
  
  def output(s: StringBuilder, indent: Int) {
    createIndent(s, indent)
    s.append("module ").append(name.name).append("\n")

    outputSeparatedList(imports, s, indent + 1, "\n")
    s.append("\n")

    outputSeparatedList(definitions, s, indent + 1, "\n")
    s.append("\n")
  }

  def getMember(name: Symbol): Option[Referable] = defsByName.get(name)


}