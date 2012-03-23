package org.skycastle.parser.syntaxtree

import org.skycastle.parser.model.{SyntaxNode, TypeDef}
import java.util.ArrayList


/**
 *
 */
trait Expr extends AstNode {

  private var _valueType: Option[TypeDef] = None

  final def valueType: Option[TypeDef] = {
    if ( !_valueType.isDefined) _valueType = determineValueType(Set(this))
    _valueType
  }

  final def valueType(visitedNodes: Set[AstNode]): Option[TypeDef] = {
    if (_valueType.isDefined) _valueType
    else if (visitedNodes.contains(this)) None
    else {
      _valueType = determineValueType(visitedNodes + this)
      _valueType
    }
  }

  protected def determineValueType(visitedNodes: Set[AstNode]): Option[TypeDef]

  
  def checkForErrors(errors: ArrayList[SyntaxError]) {
    if (valueType == null) addError(errors, "Could not determine expression type.")
  }
}