package org.skycastle.client.messaging

import handlers.{MethodActionHandler, ActionHandler}
import org.skycastle.client.{ActionMethod, ClientServices}
import org.skycastle.utils.{Logging}
import com.thoughtworks.paranamer.CachingParanamer

/**
 * A message handler that delegates messages to methods of services that are annotated to be callable with message.
 */
class MessageHandlerImpl(services: ClientServices) extends MessageHandler with Logging {

  private val paranamer = new CachingParanamer()
  private var handlers: Map[Symbol, ActionHandler] = Map()

  def onMessage(message: Message) {
    handlers.get(message.action) match {
      case Some(handlerMethod: ActionHandler) =>
        try {
          handlerMethod.handle(message)
        } catch {
          case e: Error =>
            log.warn("MESSAGE_HANDLER: Error when handling message '"+message+"': " + e.getMessage, e)
          case e: Exception =>
            log.warn("MESSAGE_HANDLER: Exception when handling message '"+message+"': " + e.getMessage, e)
        }
      case None =>
        log.warn("MESSAGE_HANDLER: Unknown action '"+message.action.name+"', for message '"+message+"', ignoring it.")
    }
  }

  def onDisconnected(reason: String, cause: Exception) {
    log.info("Disconnected from server: " + reason, cause)
  }

  def onConnectionFailed(reason: String, cause: Exception) {
    log.error("Connected failed: " + reason, cause)
  }

  def onConnected() {
    log.info("Connected to server")
  }


  override def startup() {
    // Find any message handler methods in any services
    services.services foreach {service =>
      val serviceClass = service.getClass
      serviceClass.getMethods foreach {method =>
        val handlerAnnotation: ActionMethod = method.getAnnotation(classOf[ActionMethod])
        if (handlerAnnotation != null) {
          val actionName = Symbol(method.getName)
          handlers += actionName -> MethodActionHandler(actionName, service, method, paranamer)
        }
      }
    }
  }


}