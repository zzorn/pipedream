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
   * Add a callback that is called every logic update phase.
   * The callback may manipulate spatials.
   */
  def addUpdateCallback(callback: (Float) => Unit)

  /**
   * Removes an update callback.
   */
  def removeUpdateCallback(callback: (Float) => Unit)


}
