package org.skycastle.parser.syntaxtree

import org.skycastle.parser.model.defs.Def
import org.skycastle.parser.model._
import java.util.ArrayList

/**
 *
 */
case class Parameter(name: Symbol, declaredReturnType: Option[TypeDef], defaultValue: Option[Expr]) extends Expr {

  def output(s: StringBuilder, indent: Int) {
    s.append(name.name)

    if (declaredReturnType.isDefined) {
      s.append(": ")
      declaredReturnType.get.output(s, indent)
    }

    if (defaultValue.isDefined) {
      s.append(" = ")
      defaultValue.get.output(s, indent)
    }
  }

  protected def determineValueType(visitedNodes: Set[AstNode]): TypeDef = {
    if (declaredReturnType.isDefined) declaredReturnType.get
    else if (defaultValue.isDefined) defaultValue.get.valueType(visitedNodes)
    else null
  }

  override def checkForErrors(errors: ArrayList[SyntaxError]) {
    super.checkForErrors(errors)

    // Check that declared type matches default value type.
    if (declaredReturnType.isDefined &&
        defaultValue.isDefined &&
        !declaredReturnType.get.isAssignableFrom(defaultValue.get.valueType))
      addError(errors, "Type of default value ("+defaultValue.get.valueType+") is not compatible with the declared variable type ("+declaredReturnType.get+")")
  }
}
