package org.skycastle.parser.model

/**
 *
 */
case class Definition(name: Symbol, var parameters: List[Parameter], kind: Class[_], value: Expression) extends Outputable {

  def output(s: StringBuilder, indent: Int) {
    createIndent(s, indent)
    s.append("fun ")
     .append(name.name)
     .append("(")

    outputSeparatedList(parameters, s, indent + 1)

    s.append("): ")
     .append(if (kind==null) "null" else kind.getSimpleName)
     .append(" = ")
    
    value.output(s, indent + 1)
    s.append("\n")
  }

}