package org.skycastle.world

import org.scalaprops.Bean
import java.lang.{IllegalArgumentException, IllegalStateException}
import space.Space
import java.util.HashSet
import scala.collection.JavaConversions._

/**
 * 
 */
trait Entity extends Bean {

  private val listeners = new HashSet[EntityListener]()

  private var _id: EntityId = null
  private var _space: Space = null

  def id: EntityId = _id

  def id_=(newId: EntityId) {
    if (_id != null) throw new IllegalStateException("Entity " + _id + " already has an id, can not set it a new id.")
    if (newId == null) throw new IllegalArgumentException("newId should not be null")

    _id = newId
  }

  /**
   * The space an entity is in.
   */
  def space: Space = _space

  /**
   * Change the space an entity is in.
   * Returns true on success, and false on failure.
   */
  def moveToSpace(newSpace: Space): Boolean = {
    newSpace.addEntity(this)
  }

  def getMass: Double

  /**
   * Removed an entity from the world and destroys it.
   */
  def delete() {
    if (space != null) space.removeEntity(this)
    EntityManager.remove(this)
    notifyDeleted()
    listeners.clear()
    // TODO: Remove any references to the entity from other entities
  }

  def addListener(listener: EntityListener) {
    if (listener != null) listeners.add(listener)
  }

  def removeListener(listener: EntityListener) {
    if (listener != null) listeners.remove(listener)
  }

  // TODO: Appearance

  /**
   * Should only be called from the space the entity is added to.
   */
  private [world] def setSpace(newSpace: Space) {
    _space = newSpace
  }


  protected def notifyMassChange(oldMass: Double, newMass: Double) {
    val entity = this
    listeners foreach {l => l.onMassChange(entity, oldMass, newMass)}
  }

  protected def notifyDeleted() {
    val entity = this
    listeners foreach {l => l.onDeleted(entity)}
  }

}