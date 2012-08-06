package org.skycastle.client.entity

/**
 * Notified about changes to entity.
 */
trait EntityListener {

  def onRemoved(entity: Entity)

}