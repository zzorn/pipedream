package org.skycastle.parser.model

/**
 *
 */
case class Call(function: Symbol, params: List[CallParam]) extends Expression {

  def resultType = null

  def calculation: Context => Any = {
    null
  }

  def output(s: StringBuilder, indent: Int) {
    s.append(function.name)
    
    if (!params.isEmpty) {
      s.append("(")
      outputSeparatedList(params, s, indent + 1)
      s.append(")")
    }
  }
}

