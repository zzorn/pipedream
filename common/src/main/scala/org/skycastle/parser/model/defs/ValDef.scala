package org.skycastle.parser.model.defs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model._


/**
 *
 */
case class ValDef(name: Symbol,
                  declaredReturnType: Option[TypeDef],
                  expression: Expr) extends Def  with ValueTyped with ReturnTyped {


  protected def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef = {
    if (declaredReturnType.isDefined) declaredReturnType.get
    else expression.valueType(visitedNodes)
  }

  def getMember(name: Symbol) = None

  override def subNodes = singleIt(valueType) ++ singleIt(expression)

  val javaPrefix = "VAL_"
  def valueJavaName = javaPrefix + name.name
  
  def generateJavaCode(s: StringBuilder, indent: Indenter) {
    // Header
    s.append(indent).append("public final ").append(returnType.javaType).append(" ").append(valueJavaName).append(" = ")

    // Expression
    expression.generateJavaCode(s, indent.increase())

    // Close
    s.append(";\n")
  }
}
