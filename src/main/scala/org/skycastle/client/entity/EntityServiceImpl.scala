package org.skycastle.client.entity

import org.skycastle.utils.ParameterChecker

/**
 *
 */
class EntityServiceImpl extends EntityService {

  def createEntity(regionId: Symbol, entityId: Symbol, data: Map[Symbol, Any]): Entity = {
    ParameterChecker.requireIsIdentifier(entityId, 'entityId)

    // TODO: Extract data

    new Entity(entityId, new PlaceholderAppearance(), new Location(regionId))
  }

  def updateEntity(entity: Entity, change: Map[Symbol, Any]) {
    ParameterChecker.requireNotNull(entity, 'entity)

    // TODO: Update entity
  }
}
