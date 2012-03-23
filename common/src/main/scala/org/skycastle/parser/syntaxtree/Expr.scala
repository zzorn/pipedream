package org.skycastle.parser.syntaxtree

import org.skycastle.parser.model.{SyntaxNode, TypeDef}
import java.util.ArrayList


/**
 *
 */
trait Expr extends AstNode {

  private var _valueType: TypeDef = null

  final def valueType: TypeDef = {
    if ( _valueType == null) _valueType = determineValueType(Set(this))
    _valueType
  }

  final def valueType(visitedNodes: Set[AstNode]): TypeDef = {
    if (_valueType != null) _valueType
    else if (visitedNodes.contains(this)) null
    else {
      _valueType = determineValueType(visitedNodes + this)
      _valueType
    }
  }

  protected def determineValueType(visitedNodes: Set[AstNode]): TypeDef


  def checkForErrors(errors: ArrayList[SyntaxError]) {
    if (valueType == null) addError(errors, "Could not determine expression type.")
  }
}