package org.skycastle.parser.model

/**
 *
 */
trait ValueTyped extends SyntaxNode {

  private var _valueType: TypeDef = null

  final def valueType: TypeDef = {
    if ( _valueType == null) _valueType = determineValueType(Set(this))
    _valueType
  }
  
  final def valueType(visitedNodes: Set[SyntaxNode]): TypeDef = {
    if (_valueType != null) _valueType
    else if (visitedNodes.contains(this)) null
    else {
      _valueType = determineValueType(visitedNodes + this)
      _valueType
    }
  }

  protected def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef

  override def checkForErrors(errors: Errors) {
    if (valueType == null) errors.addError("Type could not be determined", this)
  }

}