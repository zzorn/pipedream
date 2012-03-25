package org.skycastle.parser.model.module

import org.skycastle.parser.model.defs.Def
import org.skycastle.parser.model.{Indenter, SyntaxNode, PathRef, Outputable}


/**
 *
 */
final case class Import(path: PathRef, all: Boolean) extends SyntaxNode {

  var importedDef: Def = null

  def importName = path.lastName

  def getNamedImport(name: Symbol): Option[SyntaxNode] = {
    if (!all && importName == name) Some(importedDef)
    else if (all) importedDef.getNamedNode(name)
    else None
  }

  def generateJavaCode(s: StringBuilder, indent: Indenter) {
    s.append(indent).append("import ").append(path)
    
    if (all) s.append(".*")
    
    s.append(";\n")
  }
}