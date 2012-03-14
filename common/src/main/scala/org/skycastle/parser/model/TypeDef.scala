package org.skycastle.parser.model

/**
 *
 */
trait TypeDef extends Outputable {


}

case class SimpleType(typeName: Symbol, kind: Class[_]) extends TypeDef {
  def output(s: StringBuilder, indent: Int) {
    if (kind != null) s.append(kind.getSimpleName)
    else s.append(typeName.name)
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
