package org.skycastle.parser.model

/**
 *
 */
trait TypeDef extends SyntaxNode {

  def mostSpecificCommonType(other: TypeDef): TypeDef

  def mostGeneralCommonSubType(other: TypeDef): TypeDef

  def isAssignableFrom(other: TypeDef): Boolean = {
    mostSpecificCommonType(other) == this
  }

}

case class SimpleType(typeName: Symbol, kind: Class[_]) extends TypeDef {
  def output(s: StringBuilder, indent: Int) {
    s.append(typeName.name)
  }

  def mostSpecificCommonType(other: TypeDef): TypeDef = {
    if (other == null) null
    else if (other == this) this
    else if (other.isInstanceOf[SimpleType]) {
      val otherST = other.asInstanceOf[SimpleType]
      if (otherST.kind.isAssignableFrom(kind)) otherST
      else if (kind.isAssignableFrom(otherST.kind)) this
      else AnyType
    }
    else AnyType
  }

  def mostGeneralCommonSubType(other: TypeDef): TypeDef = {
    if (other == null) null
    else if (other == this) this
    else if (other.isInstanceOf[SimpleType]) {
      val otherST = other.asInstanceOf[SimpleType]
      if (otherST.kind.isAssignableFrom(kind)) this
      else if (kind.isAssignableFrom(otherST.kind)) other
      else NothingType
    }
    else AnyType
  }
}

case class FunType(parameterTypes: List[TypeDef], returnType: TypeDef) extends TypeDef {
  def output(s: StringBuilder, indent: Int) {
    s.append("(")
    outputSeparatedList(parameterTypes, s, indent)
    s.append(") -> ")
    if (returnType != null) returnType.output(s, indent)
    else s.append("[UnknownType]")
  }

  override def subNodes = parameterTypes.iterator ++ singleIt(returnType)

  def mostSpecificCommonType(other: TypeDef): TypeDef = {
    if (other == null) null
    else if (other == this) this
    else if (other.isInstanceOf[FunType]) {
      val otherFT = other.asInstanceOf[FunType]
      if (otherFT.parameterTypes.size != parameterTypes.size) AnyType 
      else {
        val params = parameterTypes.zip(otherFT.parameterTypes).map(zipped => zipped._1.mostGeneralCommonSubType(zipped._2))
        val ret = returnType.mostSpecificCommonType(otherFT.returnType)
        FunType(params, ret)
      }
    }
    else AnyType
  }

  def mostGeneralCommonSubType(other: TypeDef): TypeDef = {
    if (other == null) null
    else if (other == this) this
    else if (other.isInstanceOf[FunType]) {
      val otherFT = other.asInstanceOf[FunType]
      if (otherFT.parameterTypes.size != parameterTypes.size) NothingType
      else {
        val params = parameterTypes.zip(otherFT.parameterTypes).map(zipped => zipped._1.mostSpecificCommonType(zipped._2))
        val ret = returnType.mostGeneralCommonSubType(otherFT.returnType)
        FunType(params, ret)
      }
    }
    else NothingType
  }


}


case class ListType(elementType: TypeDef) extends TypeDef {

  def mostSpecificCommonType(other: TypeDef): TypeDef = {
    if (other == null) null
    else if (other == this) this
    else if (other.isInstanceOf[ListType]) {
      val otherLT = other.asInstanceOf[ListType]
      val et: TypeDef = elementType.mostSpecificCommonType(otherLT.elementType)
      if (et == null) null
      else ListType(et)
    }
    else AnyType
  }


  def mostGeneralCommonSubType(other: TypeDef): TypeDef = {
    if (other == null) null
    else if (other == this) this
    else if (other.isInstanceOf[ListType]) {
      val otherLT = other.asInstanceOf[ListType]
      val et: TypeDef = elementType.mostGeneralCommonSubType(otherLT.elementType)
      if (et == null) null
      else ListType(et)
    }
    else NothingType
  }


  def output(s: StringBuilder, indent: Int) {
    s.append("List[")
    elementType.output(s, indent)
    s.append("]")
  }
}

abstract class SpecialType(val name: String) extends TypeDef {
  def output(s: StringBuilder, indent: Int) {
    s.append(name)
  }
}

case object NumType extends SpecialType("Num") {
  
  def mostSpecificCommonType(other: TypeDef): TypeDef = {
    if (other == null) null
    else if (other == this) this
    else AnyType
  }

  def mostGeneralCommonSubType(other: TypeDef): TypeDef = {
    if (other == null) null
    else if (other == this) this
    else NothingType
  }
}


case object AnyType extends SpecialType("Any") {
  def mostSpecificCommonType(other: TypeDef) = AnyType
  def mostGeneralCommonSubType(other: TypeDef) = other
}

case object NothingType extends SpecialType("Nothing") {
  def mostSpecificCommonType(other: TypeDef) = other
  def mostGeneralCommonSubType(other: TypeDef) = NothingType
}

