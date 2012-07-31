package org.skycastle.client.entity

import org.skycastle.utils.Service

/**
 *
 */
trait EntityService extends Service {

  def createEntity(regionId: Symbol, entityId: Symbol, data: Map[Symbol, Any]): Entity

  def updateEntity(entity: Entity, change: Map[Symbol, Any])

}
