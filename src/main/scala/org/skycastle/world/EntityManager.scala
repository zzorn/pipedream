package org.skycastle.world

import java.util.HashMap

/**
 * Keeps track of all entities on the server
 */
// TODO: Handle persistence here too?  Saving entities that are changed?
object EntityManager {

  var uniqueHostName: Symbol = 'default

  private val entities = new HashMap[EntityId, Entity](1000)
  private var _nextFreeId: Long = 1

  def apply(id: Entity): Entity = entities.get(id)

  def get(id: Entity): Option[Entity] = {
    val e = apply(id)
    if (e == null) None
    else           Some(e)
  }

  def add(e: Entity): EntityId = {
    if (e.id == null) e.id = nextId
    entities.put(e.id, e)
    e.id
  }

  def remove(e: Entity) {
    if (e != null) remove(e.id)
  }

  def remove(id: EntityId) {
    if (id != null) entities.remove(id)
  }

  private def nextId: EntityId = {
    val id = _nextFreeId
    _nextFreeId += 1
    new EntityId(uniqueHostName, id)
  }

}