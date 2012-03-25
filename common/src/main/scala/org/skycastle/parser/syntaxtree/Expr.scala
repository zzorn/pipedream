package org.skycastle.parser.syntaxtree

import java.util.ArrayList
import runtime.{Value, DynamicContext}
import org.skycastle.parser.model.{SyntaxError, SyntaxNode, TypeDef}


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


  def calculate(): Value

  def calculate(dynamicContext: DynamicContext): Value

  
  def checkForErrors(errors: ArrayList[SyntaxError]) {
    if (valueType == null) addError(errors, "Could not determine expression type.")
  }
}