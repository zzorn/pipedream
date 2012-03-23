package org.skycastle.parser.syntaxtree

import java.util.ArrayList
import org.skycastle.parser.model.{TypeDef, FunType}

/**
 *
 */
case class Call(path: List[Symbol]) extends AstNode {

  override def checkForErrors(errors: ArrayList[SyntaxError]) {
    super.checkForErrors(errors)

    getNodeAtPath(path) match {
      case Some(exprNode: Expr) =>
        exprNode.valueType match {
          case funType: FunType => 
            // TODO: Check parameter types
          case t: TypeDef => 
            addError(errors, "Refered expression is not a function (instead it was of type '"+t+"'), can not invoke it.")
        }
      case None =>
        // Already filed error in super method.
      case Some(n: AstNode) =>
        addError(errors, "Refered node was not an expression node, can not invoke it. (Instead it was of type '"+n.getClass.getSimpleName+"').")
    }
  }

}