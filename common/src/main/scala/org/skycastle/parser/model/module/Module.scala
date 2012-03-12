package org.skycastle.parser.model.module

import org.skycastle.parser.model.defs.Def
import org.skycastle.parser.model.{Outputable}


/**
 *
 */
final case class Module(imports: List[Import], definitions: List[Def]) extends Outputable {

  def output(s: StringBuilder, indent: Int) {
    s.append("\n")

    outputSeparatedList(imports, s, indent + 1, "")
    s.append("\n")

    outputSeparatedList(definitions, s, indent + 1, "\n")
    s.append("\n")
  }
}