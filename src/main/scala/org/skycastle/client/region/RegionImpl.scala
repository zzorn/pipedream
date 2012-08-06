package org.skycastle.client.region

import org.skycastle.client.entity.Entity
import java.util
import org.skycastle.utils.ParameterChecker

class RegionImpl(regionId: Symbol) extends Region {

  private val _entities = new util.HashMap[Symbol, Entity]()
  private var listeners: Set[RegionListener] = Set()

  def addEntity(entity: Entity) {
    ParameterChecker.requireNotNull(entity, 'entity)
    if (_entities.containsKey(entity.id)) throw new Error("The region '"+regionId.name+"' already contains an entity with the id '"+entity.id+"'")

    _entities.put(entity.id, entity)

    listeners foreach {l => l.onEntityAdded(entity)}
  }

  def getEntity(entityId: Symbol): Entity = {
    ParameterChecker.requireNotNull(entityId, 'entityId)
    if (!_entities.containsKey(entityId)) throw new Error("No entity with id '"+entityId.name+"' found in the region")

    _entities.get(entityId)
  }

  def removeEntity(entityId: Symbol) {
    ParameterChecker.requireNotNull(entityId, 'entityId)

    if (_entities.containsKey(entityId)) {
      val removedEntity = _entities.get(entityId)

      _entities.remove(entityId)

      listeners foreach {l => l.onEntityRemoved(removedEntity)}
    }
  }

  def removeAll() {
    _entities.clear()
    listeners foreach {l => l.onAllEntitiesRemoved()}
  }

  def entities = _entities.values()

  def addRegionListener(listener: RegionListener) {
    listeners += listener
  }

  def removeRegionListener(listener: RegionListener) {
    listeners -= listener
  }
}
