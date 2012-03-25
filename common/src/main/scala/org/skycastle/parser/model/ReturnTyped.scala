package org.skycastle.parser.model

import defs.Parameter
import expressions.Expr
import org.skycastle.parser.Context


/**
 *
 */
trait ReturnTyped extends ValueTyped {


  def declaredReturnType: Option[TypeDef]
  def returnType: TypeDef = returnType(Set(this))
  def returnType(visited: Set[SyntaxNode]): TypeDef = {
    if (declaredReturnType.isDefined) declaredReturnType.get
    else {
      valueType(visited) match {
        case ft: FunType => ft.returnType
        case _ => null
      }
    }
  }

}