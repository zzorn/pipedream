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
  
  def javaType: String

  def generateJavaCode(s: StringBuilder, indent: Indenter) {
    s.append(javaType)
  }
}

case class ClassType(typeName: Symbol, methodInfos: Map[Symbol,  MethodInfo], javaType: Class[_]) extends TypeDef {

  def javaTypeName = javaType.getName

  def mostSpecificCommonType(other: TypeDef): TypeDef = {
    if (other == null) null
    else if (other == this) this
    else if (other.isInstanceOf[ClassType]) {
      val otherST = other.asInstanceOf[ClassType]
      if (otherST.javaType != null && otherST.javaType.isAssignableFrom(javaType)) otherST
      else if (javaType != null && javaType.isAssignableFrom(otherST.javaType)) this
      else AnyType
    }
    else AnyType
  }

  def mostGeneralCommonSubType(other: TypeDef): TypeDef = {
    if (other == null) null
    else if (other == this) this
    else if (other.isInstanceOf[ClassType]) {
      val otherST = other.asInstanceOf[ClassType]
      if (otherST.javaType != null && otherST.javaType.isAssignableFrom(javaType)) this
      else if (javaType != null && javaType.isAssignableFrom(otherST.javaType)) other
      else NothingType
    }
    else AnyType
  }

  
  def getMethod(name: Symbol): Option[MethodInfo] = methodInfos.get(name)

  
}

case class FunType(parameterTypes: List[TypeDef], returnType: TypeDef) extends TypeDef {

  val functionTypeName: String = "org.skycastle.flowlang.FuncType"
  
  override def subNodes = parameterTypes.iterator ++ singleIt(returnType)

  def javaType = functionTypeName + "<" + returnType.javaType + ">"

  def mostSpecificCommonType(other: TypeDef): TypeDef = {
    if (other == null) null
    else if (returnType == null) null
    else if (other == this) this
    else if (other.isInstanceOf[FunType]) {
      val otherFT = other.asInstanceOf[FunType]
      if (otherFT.returnType == null) null
      else {
        if (otherFT.parameterTypes.size != parameterTypes.size) AnyType
        else {
          val params = parameterTypes.zip(otherFT.parameterTypes).map(zipped => zipped._1.mostGeneralCommonSubType(zipped._2))
          val ret = returnType.mostSpecificCommonType(otherFT.returnType)
          FunType(params, ret)
        }
      }
    }
    else AnyType
  }

  def mostGeneralCommonSubType(other: TypeDef): TypeDef = {
    if (other == null) null
    else if (returnType == null) null
    else if (other == this) this
    else if (other.isInstanceOf[FunType]) {
      val otherFT = other.asInstanceOf[FunType]
      if (otherFT.returnType == null) null
      else {
        if (otherFT.parameterTypes.size != parameterTypes.size) NothingType
        else {
          val params = parameterTypes.zip(otherFT.parameterTypes).map(zipped => zipped._1.mostSpecificCommonType(zipped._2))
          val ret = returnType.mostGeneralCommonSubType(otherFT.returnType)
          FunType(params, ret)
        }
      }
    }
    else NothingType
  }


}


case class ListType(elementType: TypeDef) extends TypeDef {

  def javaType = "java.util.List<" + elementType.javaType + ">"

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


}

abstract class SpecialType(val name: String) extends TypeDef {
}

case object NumType extends SpecialType("Num") {

  def javaType = "java.lang.Double"

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


case object BoolType extends SpecialType("Bool") {

  def javaType = "java.lang.Boolean"

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
  def javaType = "java.lang.Object"

  def mostSpecificCommonType(other: TypeDef) = AnyType
  def mostGeneralCommonSubType(other: TypeDef) = other
}

case object NothingType extends SpecialType("Nothing") {
  def javaType = "java.lang.Object"

  def mostSpecificCommonType(other: TypeDef) = other
  def mostGeneralCommonSubType(other: TypeDef) = NothingType
}

