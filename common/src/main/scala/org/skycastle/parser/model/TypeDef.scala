package org.skycastle.parser.model

/**
 *
 */
trait TypeDef extends Outputable {

}

case class SimpleType(kind: Class[_]) extends TypeDef {
  def output(s: StringBuilder, indent: Int) {
    s.append(kind.getSimpleName)
  }
}

case class FunType(parameterTypes: List[TypeDef], resultType: TypeDef) extends TypeDef {
  def output(s: StringBuilder, indent: Int) {
    s.append("(")
    outputSeparatedList(parameterTypes, s, indent)
    s.append(") => ")
    resultType.output(s, indent)
  }

}
