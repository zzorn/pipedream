package org.skycastle.parser.model

/**
 *
 */
case class CallParam(paramName: Option[Symbol], var parameters: List[CallParam], value: Expression) extends Outputable {

  def output(s: StringBuilder, indent: Int) {
    if (paramName.isDefined) s.append(paramName.get.name)

    if (parameters.size > 0) {
      s.append("(")
      outputSeparatedList(parameters, s, indent + 1)
      s.append(")")
    }
    
    if (paramName.isDefined || parameters.size > 0) s.append(" = ")

    value.output(s, indent)
  }

}