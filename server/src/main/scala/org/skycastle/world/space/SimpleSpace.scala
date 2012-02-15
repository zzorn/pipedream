package org.skycastle.world.space

import java.util.{HashMap, ArrayList}
import org.skycastle.world.{EntityId, Entity}

/**
 * Just a simple unstructured space.
 */
class SimpleSpace extends Space {

  private val entities = new HashMap[EntityId, Entity]()

  def containsEntity(id: EntityId) = entities.containsKey(id)
  protected def doAdd(entity: Entity) {entities.put(entity.id, entity)}
  protected def doRemove(entityId: EntityId): Entity = entities.remove(id)

}