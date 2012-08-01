package org.skycastle.client.region

import org.skycastle.client.ClientServices
import org.skycastle.utils.ParameterChecker

class RegionServiceImpl(services: ClientServices) extends RegionService {

  var regions: Map[Symbol, Region] = Map()
  var currentRegion: Region = null


  def getRegion(regionId: Symbol): Region = {
    ParameterChecker.requireNotNull(regionId, 'regionId)
    if (!regions.contains(regionId)) throw new Error("No region named '"+regionId.name+"' found")

    regions(regionId)
  }

  def currentRegionChanged(regionId: Symbol) {
    currentRegion = getRegion(regionId)
  }

  def entityAppeared(regionId: Symbol, entityId: Symbol, entityData: Map[Symbol, Any]) {
    val region = getRegion(regionId)
    val entity = services.entityService.createEntity(regionId, entityId, entityData)
    region.addEntity(entity)
  }

  def entityChangedRegion(oldRegionId: Symbol, newRegionId: Symbol, entityId: Symbol) {
    val oldRegion = getRegion(oldRegionId)
    val newRegion = getRegion(newRegionId)

    val entity = oldRegion.getEntity(entityId)

    oldRegion.removeEntity(entityId)
    newRegion.addEntity(entity)
  }

  def entityUpdated(regionId: Symbol, entityId: Symbol, changeData: Map[Symbol, Any]) {
    val region = getRegion(regionId)
    val entity = region.getEntity(entityId)
    services.entityService.updateEntity(entity, changeData)
  }

  def entityDisappeared(regionId: Symbol, entityId: Symbol) {
    val region = getRegion(regionId)
    region.removeEntity(entityId)
  }

  def regionAppeared(regionId: Symbol, regionData: Any) {
    ParameterChecker.requireIsIdentifier(regionId, 'regionId)
    if (regions.contains(regionId)) throw new Error("A region with id '"+regionId+"' already exists.")

    val region = new RegionImpl(regionId)
    regions += regionId -> region
  }

  def regionDisappeared(regionId: Symbol) {
    val region = getRegion(regionId)
    region.removeAll()
    regions -= regionId
  }
}
