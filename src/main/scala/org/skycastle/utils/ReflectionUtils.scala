package org.skycastle.utils

import java.lang.reflect.Method

/**
 * Utilities for working with annotations / reflection.
 */
object ReflectionUtils {

  /**
   * Gets the specified annotation for the specified method, if present.
   * Checks the interfaces the method implements in addition to the classes
   * (java reflection doesn't look for annotations inherited from interfaces).
   */
  def getInheritedAnnotation[T <: java.lang.annotation.Annotation](method: Method, annotationClass: Class[T]): Option[T] = {
    // Check if the method has the annotation
    val a = method.getAnnotation(annotationClass)
    if (a != null) return Some(a)

    // Get all interfaces that the method may be present in
    val interfaces = getAllInterfaces(method.getDeclaringClass)

    // Find first interface that has the annotation
    var i = interfaces
    while (!i.isEmpty) {
      val ai = getAnnotation(i.head, method, annotationClass)
      if (ai.isDefined) return ai
      i = i.tail
    }

    // Not found
    None
  }

  private def getAnnotation[T <: java.lang.annotation.Annotation](c: Class[_], m: Method, a: Class[T]): Option[T] = {
    try {
      val mi = c.getDeclaredMethod(m.getName, m.getParameterTypes: _*)
      val ai = mi.getAnnotation(a)
      if (ai == null) None
      else Some(ai)
    } catch {
      case e: Exception => None // method not found.
    }
  }

  /**
   * @return all interfaces that a class implements, including the interfaces the interfaces extend, etc.
   */
  def getAllInterfaces(c: Class[_]): List[Class[_]] = getAllInterfacesRec(List(c))

  private def getAllInterfacesRec(cs: List[Class[_]]): List[Class[_]] = {
    cs flatMap {c =>
      val interfaces: List[Class[_]] = c.getInterfaces.toList
      interfaces ::: getAllInterfacesRec(interfaces)
    }
  }

}