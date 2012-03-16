package org.skycastle.parser.model.module

import org.skycastle.parser.model.{SyntaxNode, PathRef, Outputable}
import org.skycastle.parser.model.defs.Def


/**
 *
 */
final case class Import(path: PathRef, all: Boolean) extends SyntaxNode {

  var importedDef: Def = null

  def output(s: StringBuilder, indent: Int) {
    createIndent(s, indent)
    s.append("import ")
    s.append(path)
    if (all) s.append(".*")
  }

  def importName = path.lastName

  def getNamedImport(name: Symbol): Option[Def] = {
    if (!all && importName == name) Some(importedDef)
    else if (all) importedDef.getContextNamedDef(name)
    else None
  }
  
}