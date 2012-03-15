package org.skycastle.parser.model.module

import org.skycastle.parser.model.{PathRef, Outputable}


/**
 *
 */
final case class Import(path: PathRef) extends Outputable {

  def output(s: StringBuilder, indent: Int) {
    createIndent(s, indent)
    s.append("import ")
    s.append(path)
    s.append("\n")
  }
}