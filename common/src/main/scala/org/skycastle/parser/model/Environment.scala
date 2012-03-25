package org.skycastle.parser.model

/**
 *
 */
class Environment {

  private var _classes: Map[Symbol, ClassType] = Map()

  addClassType(new ClassType())

  def addClassType(classType: ClassType) {
    _classes += classType.typeName -> classType
  }
  
  def availableClassTypes: Map[Symbol, ClassType] = _classes
  
}