package org.skycastle.parser

import model.{Value, PathRef}

/**
 *
 */
trait Context {
  
  def parentContext: Context = null
  
  def getNestedValue(name: Symbol): Option[Value] = None

  final def getValue(path: List[Symbol]): Option[Value] = {
    getNestedValue(path.head) match {
      case Some(value) =>
        if (path.tail == Nil) Some(value)
        else value.getValue(path.tail)
      case None =>
        if (parentContext != null) parentContext.getValue(path)
        else None
    }
  }

}