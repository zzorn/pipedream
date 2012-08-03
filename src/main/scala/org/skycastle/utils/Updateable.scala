package org.skycastle.utils

import org.scalastuff.scalabeans.Preamble._
import org.scalastuff.scalabeans.{PropertyDescriptor, MutablePropertyDescriptor}
import org.skycastle.client.UpdatingField

/**
 * Something that can have fields updated based on a message.
 */
trait Updateable extends Logging {

  private var listeners: Map[Symbol, List[PropertyListener]] = Map()

  def update(data: Map[Symbol, Any]) {
    val descriptor = DescriptorCache(getClass)

    var changedFields: List[String] = Nil
    var updatedFields: List[String] = Nil
    data foreach {entry =>
      val nameSymbol = entry._1
      val name = nameSymbol.name
      val value: Any = entry._2
      descriptor.property(name) match {
        case None => log.warn("No property named '"+name+"' found in "+getClass + ", can not update with '"+value+"', when trying to do an update with values " + data)
        case Some(property) => {

          // If the property value implements Updateable, and the value is a map, forward the value to its update function.
          if (isUpdateableProperty(property) && classOf[Map[Symbol, Any]].isInstance(value)) {
            property.get(this).asInstanceOf[Updateable].update(value.asInstanceOf[Map[Symbol, Any]])
            updatedFields ::= property.name
          }
          // Check that value can be set
          else if (!property.isInstanceOf[MutablePropertyDescriptor]) {
            log.warn("Property '"+name+"' in "+getClass + " is immutable, can not update with '"+value+"', when trying to do an update with values " + data)
          }
          else if (property.findAnnotation[UpdatingField] == null) {
            log.warn("Property '"+name+"' in "+getClass + " is not annotated with "+classOf[UpdatingField].getSimpleName+", can not update with '"+value+"', when trying to do an update with values " + data)
          }
          else {
            // Get old value
            val oldValue: Any = property.get(this)

            if (oldValue != value) {
              // Set value
              property.asInstanceOf[MutablePropertyDescriptor].set(this, value)
              changedFields ::= property.name

              // Notify listeners
              if (listeners.contains(nameSymbol)) {
                val propListeners: List[PropertyListener] = listeners(nameSymbol)
                propListeners foreach {listener =>
                  listener.apply(nameSymbol, this, oldValue, value)
                }
              }
            }
          }
        }
      }
    }

    // Notify object about updates
    if (!changedFields.isEmpty || !updatedFields.isEmpty) {
      onUpdate(changedFields, updatedFields)
    }
  }

  def addListener(property: Symbol, listener: PropertyListener) {
    listeners += property -> (listener :: listeners.getOrElse(property, Nil))
  }

  def removeListener(property: Symbol, listener: PropertyListener) {
    listeners += property -> (listeners.getOrElse(property, Nil).filterNot(_ == listener))
  }

  /**
   * Called when any fields are changed or updated by server.
   */
  protected def onUpdate(changedFields: List[String], updatedFields: List[String]) {}


  private def isUpdateableProperty(property: PropertyDescriptor): Boolean = {
    property.get(this) != null &&
    classOf[Updateable].isAssignableFrom(property.scalaType.erasure)
  }
}

