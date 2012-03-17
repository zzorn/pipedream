package org.skycastle.parser.model.defs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.{SyntaxNode, TypeDef}


/**
 *
 */
case class ValDef(name: Symbol,
                  initialTypeDef: Option[TypeDef],
                  value: Expr) extends Def {
  
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
    if (initialTypeDef.isDefined) initialTypeDef.get
    else value.valueType(visitedNodes)
  }

  def getMember(name: Symbol) = None

  override def subNodes = singleIt(valueType) ++ singleIt(value)

}
