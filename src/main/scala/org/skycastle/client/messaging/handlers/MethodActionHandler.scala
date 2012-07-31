package org.skycastle.client.messaging.handlers

import com.thoughtworks.paranamer.Paranamer
import java.lang.reflect.Method
import org.skycastle.client.messaging.Message

/**
 * Action handler that invokes a method.
 */
case class MethodActionHandler(actionName: Symbol, host: AnyRef, method: Method, paranamer: Paranamer) extends ActionHandler {

  val parameterNames: Array[Symbol] = extractParameterNames

  def handle(message: Message) {
    val paramValues = parameterNames map {
      name => message.parameters.getOrElse(name, throw new Error("No value specified for handler method parameter '" + name.name + "' in the message."))
    }
    method.invoke(host, paramValues)
  }

  private def extractParameterNames: Array[Symbol] = {
    paranamer.lookupParameterNames(method) map {
      name => Symbol(name)
    }
  }

}
