package org.skycastle.client.region

import org.skycastle.client.entity.Entity

/**
 *
 */
trait RegionListener {

  def onEntityAdded(entity: Entity)
  def onEntityRemoved(entity: Entity)
  def onAllEntitiesRemoved()

}