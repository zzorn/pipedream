package org.skycastle.world

/**
 * Someone notified of entity changes.
 */
trait EntityListener {

  def onMassChange(entity: Entity, oldMass: Double, newMass: Double) {}
  def onDeleted(entity: Entity) {}
}