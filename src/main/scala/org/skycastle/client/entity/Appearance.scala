package org.skycastle.client.entity

import org.skycastle.client.{Client, ClientServices}
import com.jme3.scene.{Node, Spatial}
import org.skycastle.utils.Updateable

/**
 *
 */
trait Appearance extends Updateable {

  private val node: Node = new Node("Appearance")
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
    if (spatial == null) {
      spatial = createSpatial(services)
      node.attachChild(spatial)
    }

    node
  }

  // TODO: Also getter for collision shape?

  protected def createSpatial(services: ClientServices): Spatial

  override protected def onUpdate(changedFields: List[String], updatedFields: List[String]) {
    if (spatial != null) {
      node.detachChild(spatial)
      spatial = createSpatial(Client)
      node.attachChild(spatial)
    }
  }
}
