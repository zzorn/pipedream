package org.skycastle.client.shapes.loader

import org.yaml.snakeyaml.constructor.Constructor
import org.yaml.snakeyaml.TypeDescription
import java.lang.Class
import org.skycastle.client.shapes.components._
import org.skycastle.util.Logging

/**
 *
 */
class FilterConstructor(rootClass: Class[_ <: AnyRef]) extends Constructor(rootClass) with Logging {

  private var mappings: Map[String, Class[_ <: AnyRef ]] = Map()

  var source = "unknown"


  def registerType[T <: AnyRef](implicit m: Manifest[T]) {

    val kind: Class[T] = m.erasure.asInstanceOf[Class[T]]
    val tag: String = "!" + m.erasure.getSimpleName
    addTypeDescription(new TypeDescription(kind, tag))
    mappings += kind.getName -> kind

    log.info("Registered tag '"+tag+"' for type '"+kind.getName +"'")
    println("Registered tag '"+tag+"' for type '"+kind.getName +"'")
  }
  
  override def getClassForName(name: String): Class[_] = {
    
    if (mappings.contains(name)) mappings(name)
    else throw new FilterException(name, source)
  }
}
