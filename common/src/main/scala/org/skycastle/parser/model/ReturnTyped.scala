package org.skycastle.parser.model


/**
 *
 */
trait ReturnTyped extends ValueTyped {

  def declaredReturnType: Option[TypeDef]
  
  def returnType: TypeDef = returnType(Set(this))

  def returnType(visited: Set[SyntaxNode]): TypeDef = {
    if (declaredReturnType.isDefined) declaredReturnType.get
    else {
      val vt: TypeDef = valueType(visited)
      if (vt == null) null
      else if (!vt.isInstanceOf[FunType]) null
      else vt.asInstanceOf[FunType].returnType
    }
  }

}