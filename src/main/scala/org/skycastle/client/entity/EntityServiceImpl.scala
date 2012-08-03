package org.skycastle.client.entity

import org.skycastle.utils.ParameterChecker
import java.util

// TODO: Update regions with the entities that are in it, also when entity region changes.
class EntityServiceImpl extends EntityService {

  private val entities: util.HashMap[Symbol, Entity] = new util.HashMap[Symbol, Entity]()


  def addEntity(entityId: Symbol, data: Map[Symbol, Any]) {
    ParameterChecker.requireIsIdentifier(entityId, 'entityId)
    if (entities.containsKey(entityId)) throw new Error("An entity with the id '"+entityId.name+"' already exists")

    // Create entity
    val entity = new Entity(entityId, new PlaceholderAppearance(), new Location())

    // Apply data
    entity.update(data)
  }

  def updateEntity(entityId: Symbol, change: Map[Symbol, Any]) {
    ParameterChecker.requireIsIdentifier(entityId, 'entityId)
    ParameterChecker.requireNotNull(change, 'change)

    // Get entity
    val entity: Entity = entities.get(entityId)
    if (entity == null) throw new Error("No entity with the id '"+entityId.name+"' found")

    // Update
    entity.update(change)
  }

  def removeEntity(entityId: Symbol) {
    ParameterChecker.requireIsIdentifier(entityId, 'entityId)

    entities.remove(entityId)
  }
}
