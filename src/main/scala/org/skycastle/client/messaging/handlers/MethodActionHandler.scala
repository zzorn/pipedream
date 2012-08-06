package org.skycastle.client.messaging.handlers

import com.thoughtworks.paranamer.Paranamer
import java.lang.reflect.Method
import org.skycastle.client.messaging.Message
import org.skycastle.utils.Logging

/**
 * Action handler that invokes a method.
 */
case class MethodActionHandler(actionName: Symbol, host: AnyRef, method: Method, paranamer: Paranamer) extends ActionHandler with Logging {

  val parameterNames: List[Symbol] = extractParameterNames.toList

  def handle(message: Message) {
    val paramValues = parameterNames map {
      name => message.parameters.getOrElse(name, throw new Error("No value specified for handler method parameter '" + name.name + "' in the message."))
    }
    log.debug("Invoking " + actionName.name + " with parameters (" + paramValues.mkString(", ") + "), for parameter names (" + parameterNames.mkString(", ") + ")")
    log.debug("Method has param types (" + method.getParameterTypes.mkString(", ") + ")")

    // TODO: Need to box primitive types

    method.invoke(host, paramValues.asInstanceOf[List[AnyRef]] : _*)
  }

  private def extractParameterNames: Array[Symbol] = {
    paranamer.lookupParameterNames(method) map {
      name => Symbol(name)
    }
  }

}
