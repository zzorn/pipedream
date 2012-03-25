package org.skycastle.parser.model.defs

import org.skycastle.parser.model._
import expressions.Expr
import collection.immutable.List
import refs.Arg
import org.skycastle.parser.{Context,  RunError}


/**
 *
 */
case class FunDef(name: Symbol,
                  parameters: List[Parameter],
                  declaredReturnType: Option[TypeDef],
                  expression: Expr) extends Def with Callable {

  // TODO: Use identifier case class for names, that checks that they are valid java identifiers.

  override def subNodes = parameters.iterator ++ declaredReturnType.iterator ++ singleIt(expression)

  protected def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef = {
    val retType = returnType
    if (retType == null) null
    else FunType(parameters.map(p => p.valueType(visitedNodes)), retType)
  }

  override def checkForErrors(errors: Errors) {
    super.checkForErrors(errors)
    
    // TODO
  }

  def funJavaName = name.name
  
  def generateJavaCode(s: StringBuilder, indent: Indenter) {
    // Header
    s.append(indent).append("public final ").append(returnType.javaType).append(" ").append(funJavaName).append("(")
    val bodyIndent: Indenter = indent.increase()

    // Parameters
    outputSeparatedList(parameters, s, bodyIndent, ", ")

    s.append(") {\n")

    // Initialize default parameters with no value provided (=null value)
    val defaultValueIndent: Indenter = bodyIndent.increase()
    parameters foreach {p =>
      if (p.defaultValue.isDefined) {
        s.append(bodyIndent).append("if (" + p.parameterJavaName + " == null) {\n" +
          defaultValueIndent + p.parameterJavaName + " = ")
        p.defaultValue.get.generateJavaCode(s, defaultValueIndent)
        s.append(";\n").append(bodyIndent).append("}\n")
      }
    }  

    // Expression
    expression.generateJavaCode(s, bodyIndent)

    // Close
    s.append(indent).append("}\n")
  }
}