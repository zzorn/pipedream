package org.skycastle.client.region

import org.skycastle.client.entity.Entity


/**
 *
 */
trait Region {

  def addEntity(entity: Entity)

  def getEntity(entityId: Symbol): Entity

  def removeEntity(entityId: Symbol)
  def removeAll()


}
