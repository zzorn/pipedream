package org.skycastle.client.messaging

import handlers.{MethodActionHandler, ActionHandler}
import org.skycastle.client.{ActionMethod, ClientServices}
import org.skycastle.utils.{ReflectionUtils, Logging}
import com.thoughtworks.paranamer.{BytecodeReadingParanamer, DefaultParanamer, CachingParanamer}
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * A message handler that delegates messages to methods of services that are annotated to be callable with message.
 * All handler calls happen during the game engine update phase.
 */
class MessageHandlerImpl(services: ClientServices) extends MessageHandler with Logging {

  private val paranamer = new CachingParanamer(new BytecodeReadingParanamer)
  private var handlers: Map[Symbol, ActionHandler] = Map()
  private val messageQue: ConcurrentLinkedQueue[Message] = new ConcurrentLinkedQueue[Message]()

  def onMessage(message: Message) {
    messageQue.add(message)
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
    // Register message handler that runs in game engine update phase
    services.engine.addUpdateCallback({duration =>
      var messagesToHandle = messageQue.size()
      while (messagesToHandle > 0) {
        val message = messageQue.poll()
        if (message != null) {
          handleMessage(message)
        }
        messagesToHandle -= 1
      }
    })

    // Find all message handler methods in all services
    services.services foreach {service =>
      val serviceClass = service.getClass
      serviceClass.getMethods foreach {method =>
        val handlerAnnotation = ReflectionUtils.getInheritedAnnotation(method, classOf[ActionMethod])
        if (handlerAnnotation.isDefined) {
          val actionName = Symbol(method.getName)
          val handler: MethodActionHandler = MethodActionHandler(actionName, service, method, paranamer)
          handlers += actionName -> handler
          log.info("  Added message handler: " + handler)
        }
      }
    }

  }

  private def handleMessage(message: Message) {
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

}