package org.skycastle.parser.model

/**
 *
 */
final case class Block(definitions: List[Definition], values: List[Expression]) extends Expression {

  def resultType = null // List of values, or the type of the value if there is just one

  def calculation: Context => Any = {
    null
  }

  def output(s: StringBuilder, indent: Int) {
    s.append("\n")
    createIndent(s, indent - 1)
    s.append("{")
    s.append("\n")

    definitions foreach { d =>
      d.output(s, indent)
    }
    s.append("\n")

    createIndent(s, indent)
    outputSeparatedList(values, s, indent, "\n" + indentString(indent))
    s.append("\n")

    createIndent(s, indent - 1)
    s.append("}")
    s.append("\n")
  }

}