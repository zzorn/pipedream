package org.skycastle.client.entity

import org.skycastle.client.ClientServices
import com.jme3.scene.Spatial
import org.skycastle.utils.Updateable

/**
 *
 */
trait Appearance extends Updateable {

  private var spatial: Spatial = null

  /**
   * Called to update
   * @param props
   */
  def updateVisualState(props: Map[Symbol, Any])

  /**
   * @return the spatial for this appearance instance.
   */
  def getSpatial(services: ClientServices): Spatial = {
    if (spatial == null) spatial = createSpatial(services)
    spatial
  }

  // TODO: Also getter for collision shape?

  protected def createSpatial(services: ClientServices): Spatial

}
