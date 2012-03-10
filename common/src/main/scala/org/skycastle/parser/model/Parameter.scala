package org.skycastle.parser.model

/**
 *
 */
case class Parameter(name: Symbol, var parameters: List[Parameter], kind: Class[_], defaultValue: Option[Expression]) extends Outputable {
  
  def output(s: StringBuilder, indent: Int) {
    s.append(name.name)

    if (parameters.size > 0) {
      s.append("(")
      outputSeparatedList(parameters, s, indent + 1)
      s.append(")")
    }

    s.append(": ")
    s.append(if (kind==null) "null" else kind.getSimpleName)

    if (defaultValue.isDefined) {
      s.append(" = ")
      defaultValue.get.output(s, indent)
    }
  }
}