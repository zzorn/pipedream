package org.skycastle.client.region

import org.skycastle.client.ClientServices
import org.skycastle.utils.{PropertyListener, ParameterChecker}
import org.skycastle.client.entity.Entity

class RegionServiceImpl(services: ClientServices) extends RegionService {

  var regions: Map[Symbol, Region] = Map()
  var currentRegion: Region = null

  def addEntity(entity: Entity) {
    ParameterChecker.requireNotNull(entity, 'entity)

    // Listen to region id changes
    entity.location.addListener('regionId, new PropertyListener {
      def apply(propertyName: Symbol, obj: Any, oldValue: Any, newValue: Any) {
        val oldRegionId: Symbol = oldValue.asInstanceOf[Symbol]
        val newRegionId: Symbol = newValue.asInstanceOf[Symbol]
        if (regions.contains(oldRegionId)) regions(oldRegionId).removeEntity(entity.id)
        if (regions.contains(newRegionId)) regions(newRegionId).addEntity(entity)
      }
    })

    // Listen to entity removal
    entity.addRemovalListener(handleEntityRemoved)
  }

  def getRegion(regionId: Symbol): Region = {
    ParameterChecker.requireNotNull(regionId, 'regionId)
    if (!regions.contains(regionId)) throw new Error("No region named '"+regionId.name+"' found")

    regions(regionId)
  }

  def hasRegion(regionId: Symbol) = regions.contains(regionId)

  def currentRegionChanged(regionId: Symbol) {
    currentRegion = getRegion(regionId)
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

  private def handleEntityRemoved(entity: Entity) {
    val regionId = entity.location.regionId
    if (regions.contains(regionId)) {
      regions(regionId).removeEntity(entity.id)
    }
  }
}
