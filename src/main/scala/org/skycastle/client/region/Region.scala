package org.skycastle.client.region

import org.skycastle.client.entity.Entity


/**
 * Some section or volume of game world, containing entities and portals to other regions.
 */
trait Region {

  def addEntity(entity: Entity)

  def getEntity(entityId: Symbol): Entity

  def removeEntity(entityId: Symbol)

  def removeAll()

  def entities: java.util.Collection[Entity]

  def addRegionListener(listener: RegionListener)
  def removeRegionListener(listener: RegionListener)

}
