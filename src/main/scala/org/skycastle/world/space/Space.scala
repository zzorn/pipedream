package org.skycastle.world.space

import org.skycastle.world.{EntityId, EntityListener, Entity}

/**
 * 
 */
trait Space extends Entity {

  private var _containedMass = 0.0

  private val entityListener: EntityListener = new EntityListener {
    def onMassChange(entity: Entity, oldMass: Double, newMass: Double) {
      changeMass(newMass - oldMass)
    }
  }

  final def containsEntity(entity: Entity): Boolean = containsEntity(entity.id)
  def containsEntity(id: EntityId): Boolean

  /**
   * Adds the specified entity to this Space, and removes it from any previous space it was in.
   */
  final def addEntity(entity: Entity): Boolean = {
    if (entity != null && !containsEntity(entity) && canAdd(entity)) {
      val oldSpace: Space = entity.space
      if (oldSpace != null) oldSpace.removeEntity(entity)
      doAdd(entity)
      entity.setSpace(this)
      onAdded(entity)
      true
    }
    else false
  }

  /**
   * Removes specified entity from this space if it was in it.
   */
  final def removeEntity(entity: Entity) {removeEntity(entity.id)}

  /**
   * Removes the entity with the specified id from this space if it was in it.
   * Returns true on success, or if there was no such entity, false if the entity could not be removed.
   */
  final def removeEntity(id: EntityId) {
    if (containsEntity(id)) {
      val removedEntity = doRemove(id)
      onRemoved(removedEntity)
    }
  }

  override def getMass: Double = {
    super.getMass + containedMass
  }

  def containedMass: Double = _containedMass

  protected final def onAdded(entity: Entity) {
    changeMass(entity.getMass)
    entity.addListener(entityListener)
  }

  protected final def onRemoved(entity: Entity) {
    changeMass(-entity.getMass)
    entity.removeListener(entityListener)
  }

  private def changeMass(delta: Double) {
    val old = _containedMass
    _containedMass += delta
    notifyMassChange(old, _containedMass)
  }


  /**
   * True if specified entity can be added, assuming it doesn't already exists in the space.
   * Defaults to true
   */
  protected def canAdd(entity: Entity): Boolean = true

  /**
   * Does actual addition of the entity.
   */
  protected def doAdd(entity: Entity)

  /**
   * Does actual removal of the entity.
   */
  protected def doRemove(entityId: EntityId): Entity

}