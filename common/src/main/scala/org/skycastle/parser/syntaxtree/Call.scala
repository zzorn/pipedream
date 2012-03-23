package org.skycastle.parser.syntaxtree

import java.util.ArrayList
import org.skycastle.parser.model.{TypeDef, FunType}

/**
 *
 */
case class Call(path: List[Symbol]) extends AstNode with Reference with Expr {

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
}