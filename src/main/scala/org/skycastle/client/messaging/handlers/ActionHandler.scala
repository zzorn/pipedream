package org.skycastle.client.messaging.handlers

import org.skycastle.client.messaging.Message

/**
 * Handler for some type of actions.
 */
trait ActionHandler {

  def actionName: Symbol

  def handle(message: Message)

}
