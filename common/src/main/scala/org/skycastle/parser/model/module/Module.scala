package org.skycastle.parser.model.module

import org.skycastle.parser.model.defs.Def
import org.skycastle.parser.model.{MutableContext, NothingType, SyntaxNode, Outputable}
import org.skycastle.parser.Context


/**
 *
 */
final case class Module(name: Symbol, imports: List[Import], var definitions: List[Def]) extends Def {

  private var definitionsByName: Map[Symbol, Def] = definitions.map(d => d.name -> d).toMap

  def addDefinition(definition: Def) {
    if (definitionsByName.contains(definition.name)) {
      throw new IllegalArgumentException("Module '"+name+"' already contains a definition with the name '"+definition.name+"'")
    }

    definitionsByName += definition.name -> definition
    definitions = definitions ::: List(definition)
  }

  def output(s: StringBuilder, indent: Int) {
    createIndent(s, indent)
    s.append("module ").append(name.name).append(" {\n")

    outputTerminatedList(imports, s, indent + 1, "\n")

    if (!imports.isEmpty && !definitions.isEmpty) s.append("\n")

    outputTerminatedList(definitions, s, indent + 1, "\n")

    createIndent(s, indent)
    s.append("}\n")
  }

  def getImportedDef(name: Symbol): Option[Def] = {
    imports.find(_.getNamedImport(name).isDefined).flatMap(_.getNamedImport(name))
  }
  

  override def subNodes = imports.iterator ++ definitions.iterator

  override def getMember(name: Symbol): Option[Def] = definitionsByName.get(name)

  override def hasContext = true
  override def getContextNamedDef(name: Symbol): Option[Def] = definitionsByName.get(name).orElse(getImportedDef(name))


  protected def determineValueType(visitedNodes: Set[SyntaxNode]) = NothingType

  // TODO: Do module calculate have to support context?  Can we simplify the whole calculation?
  def calculate(context: Context) = ModuleValue(this)
}