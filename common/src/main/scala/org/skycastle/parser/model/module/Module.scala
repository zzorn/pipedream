package org.skycastle.parser.model.module

import org.skycastle.parser.model.defs.Def
import org.skycastle.parser.Context
import org.skycastle.parser.model._


/**
 *
 */
case class Module(name: Symbol, imports: List[Import], definitions: List[Def]) extends SyntaxNode with PackageContent {

  val ModulePrefix = "MODULE_"

  private val definitionsByName: Map[Symbol, Def] = definitions.map(d => d.name -> d).toMap

  override def getNamedNode(name: Symbol) = definitionsByName.get(name).orElse(getImportedDef(name))


  def getImportedDef(name: Symbol): Option[Def] = {
    imports.find(_.getNamedImport(name).isDefined).flatMap(_.getNamedImport(name))
  }


  override def subNodes = imports.iterator ++ definitions.iterator

  protected def determineValueType(visitedNodes: Set[SyntaxNode]) = NothingType

  override def checkForErrors(errors: Errors) {
    super.checkForErrors(errors)

    // TODO: Check that imports exist
  }

  def className: String = ModulePrefix + name.name

  final def generateJavaCode(): String = {
    val s: StringBuilder = new StringBuilder()
    generateJavaCode(s, new Indenter(0))
    s.toString()
  }

  def generateJavaCode(s: StringBuilder, indent: Indenter) {
    outputTerminatedList(imports, s, indent, "")

    s.append("\n").append(indent).append("public class ").append(className).append(" {\n")

    outputTerminatedList(definitions, s, indent.increase(), "\n")
    
    s.append(indent).append("\n}")
  }
}