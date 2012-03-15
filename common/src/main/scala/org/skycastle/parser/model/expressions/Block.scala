package org.skycastle.parser.model.expressions

import org.skycastle.parser.model.defs.Def
import org.skycastle.parser.model.Context


/**
 *
 */
final case class Block(definitions: List[Def], value: Expr) extends Expr {

  def resultType = null // List of values, or the type of the value if there is just one

  def calculation: Context => Any = {
    null
  }

  def output(s: StringBuilder, indent: Int) {
    //s.append("\n")
    //createIndent(s, indent - 1)
    s.append("{")
    s.append("\n")

    if (!definitions.isEmpty) {
      outputTerminatedList(definitions, s, indent, "\n")
    }

    createIndent(s, indent)
    s.append("return ")
    value.output(s, indent)
    s.append("\n")

    createIndent(s, indent - 1)
    s.append("}")
//    s.append("\n")
  }

}