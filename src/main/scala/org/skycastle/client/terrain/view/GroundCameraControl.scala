package org.skycastle.client.terrain.view

import com.jme3.scene.Spatial
import com.jme3.renderer.{Camera, ViewPort, RenderManager}
import com.jme3.scene.control.{Control, AbstractControl}

/**
 *
 */
class GroundCameraControl(camera: Camera) extends AbstractControl {

  def cloneForSpatial(spatial: Spatial): Control = new GroundCameraControl(camera)

  def controlUpdate(tpf: Float) {
    // Get new camera pos, notify terrain about it
    val spatial: Spatial = getSpatial
    if (spatial != null && spatial.isInstanceOf[Ground]) {
      spatial.asInstanceOf[Ground].updateCameraPos(camera.getLocation, tpf)
    }
  }

  def controlRender(rm: RenderManager, vp: ViewPort) {
    // Nothing to do
  }
}