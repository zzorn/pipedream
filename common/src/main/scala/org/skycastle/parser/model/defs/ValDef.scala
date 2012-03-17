package org.skycastle.parser.model.defs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.{ReturnTyped, ValueTyped, SyntaxNode, TypeDef}


/**
 *
 */
case class ValDef(name: Symbol,
                  declaredReturnType: Option[TypeDef],
                  value: Expr) extends Def  with ValueTyped with ReturnTyped {
  
  def output(s: StringBuilder, indent: Int) {
    createIndent(s, indent)
    s.append("val ")
    s.append(name.name)

    if (valueType != null) {
      s.append(": ")
      valueType.output(s, indent)
    }

    s.append(" = ")
    value.output(s, indent)
  }


  protected def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef = {
    if (declaredReturnType.isDefined) declaredReturnType.get
    else value.valueType(visitedNodes)
  }

  def getMember(name: Symbol) = None

  override def subNodes = singleIt(valueType) ++ singleIt(value)

}
