package org.skycastle.client.engine

import com.jme3.asset.AssetManager
import org.skycastle.utils.Service
import org.skycastle.client.entity.Entity
import org.skycastle.client.region.Region

/**
 * The 3D display engine.
 */
trait Engine extends Service {

  /**
   * @return asset manager used to load things.
   */
  def getAssetManager: AssetManager

  /**
   * Specify the entity that the camera follows
   */
  def setFocusEntity(entity: Entity)

  /**
   * Shows the region and all entities in it.
   */
  def addRegion(region: Region)

  /**
   * Removes the specified region from view.
   */
  def removeRegion(region: Region)


}
