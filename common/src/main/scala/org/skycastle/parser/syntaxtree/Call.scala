package org.skycastle.parser.syntaxtree

import java.util.ArrayList
import org.skycastle.parser.model.refs.Arg
import org.skycastle.parser.RunError
import runtime._
import org.skycastle.parser.model.{SyntaxError, TypeDef, FunType}

/**
 *
 */
case class Call(path: List[Symbol], arguments: List[Argument]) extends AstNode with Reference with Expr {

  def childNodes = arguments.iterator

  override def checkForErrors(errors: ArrayList[SyntaxError]) {
    super.checkForErrors(errors)

    referencedNode match {
      case Some(exprNode: Expr) =>
        exprNode.valueType match {
          case Some(funType: FunType) => 
            // TODO: Check parameter types
          case Some(t: TypeDef) =>
            addError(errors, "Refered expression is not a function (instead it was of type '"+t+"'), can not invoke it.")
          case None =>
            addError(errors, "Refered expression has no know type, can not invoke it.")
        }
      case None =>
        // Already filed error in super method.
      case Some(n: AstNode) =>
        addError(errors, "Refered node was not an expression node, can not invoke it. (Instead it was of type '"+n.getClass.getSimpleName+"').")
    }
  }

  def output(s: StringBuilder, indent: Int) {

  }

  protected def determineValueType(visitedNodes: Set[AstNode]): Option[TypeDef] = {
    referencedNode  match {
      case Some(exprNode: Expr) =>
        exprNode.valueType match {
          case Some(funType: FunType) => Some(funType.returnType)
          case _ => None
        }
      case _ => None
    }
  }

  def calculate(dynamicContext: DynamicContext): Value = {
    val target: Invokable = referencedNode.get.asInstanceOf[Expr].calculate().asInstanceOf[Invokable]
    
    val argumentValues = new MutableDynamicContext()

    // Bind argument names to argument values
    var index = 0
    arguments foreach {a: Argument =>
      val paramValue: Value = a.valueExpr.calculate(dynamicContext)
      if (a.paramName.isDefined) {
        // Named argument
        argumentValues.addBinding(a.paramName.get, paramValue)
      }
      else {
        // Indexed argument
        argumentValues.addBinding(target.parameters(index).name, paramValue)
        index += 1
      }
    }

    target.invoke(argumentValues)
  }



}