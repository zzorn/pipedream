package org.skycastle.parser.model.module

import org.skycastle.parser.model.Outputable

/**
 *
 */
final case class Import(location: String) extends Outputable {

  def output(s: StringBuilder, indent: Int) {
    createIndent(s, indent)
    s.append("import ")
    s.append(location)
    s.append("\n")
  }
}