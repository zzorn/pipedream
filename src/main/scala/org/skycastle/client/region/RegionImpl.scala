package org.skycastle.client.region

import org.skycastle.client.entity.Entity
import java.util
import org.skycastle.utils.ParameterChecker

class RegionImpl(regionId: Symbol) extends Region {

  private val entities = new util.HashMap[Symbol, Entity]()

  def addEntity(entity: Entity) {
    ParameterChecker.requireNotNull(entity, 'entity)

    entities.put(entity.id, entity)
  }

  def getEntity(entityId: Symbol): Entity = {
    ParameterChecker.requireNotNull(entityId, 'entityId)
    if (!entities.containsKey(entityId)) throw new Error("No entity with id '"+entityId.name+"' found in the region")

    entities.get(entityId)
  }

  def removeEntity(entityId: Symbol) {
    ParameterChecker.requireNotNull(entityId, 'entityId)

    entities.remove(entityId)
  }

  def removeAll() {
    entities.clear()
  }

}
