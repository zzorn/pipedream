package org.skycastle.client.sky

import com.jme3.asset.AssetManager
import com.jme3.scene.shape.Sphere
import com.jme3.renderer.queue.RenderQueue.Bucket
import com.jme3.bounding.BoundingSphere
import com.jme3.material.Material
import com.jme3.app.Application._
import com.jme3.scene.control.AbstractControl
import com.jme3.scene.{Node, Geometry, Spatial}
import com.jme3.renderer.{Camera, RenderManager, ViewPort}
import com.jme3.light.DirectionalLight
import com.jme3.math.{ColorRGBA, Vector3f}

/**
 * A sky-sphere that follows the specified camera.
 */
class Sky(camera: Camera, assetManager: AssetManager) extends Node {

  init()

  def createLights(node: Spatial) {
    val sun = new DirectionalLight();
    val sunDir: Vector3f = new Vector3f(1, -1, -2).normalizeLocal()
    sun.setDirection(sunDir);
    sun.setColor(new ColorRGBA(1f, 0.9f, 0.7f, 1f));
    node.addLight(sun)

    val ambient = new DirectionalLight();
    ambient.setDirection(new Vector3f(-1, -1, 2).normalizeLocal());
    ambient.setColor(new ColorRGBA(0.2f, 0.4f, 0.6f, 1f))
    node.addLight(ambient)
  }

  private def init() {
    //this.viewPort.setBackgroundColor(new ColorRGBA(0.3f, 0.3f, 0.3f, 1f));
    //this.viewPort.setBackgroundColor(new ColorRGBA(0.3f, 0.3f, 0.3f, 1f));

    val sphereMesh = new Sphere(32, 32, 100f, false, true)
    val sky = new Geometry("Sky", sphereMesh)
    sky.setQueueBucket(Bucket.Sky)
    sky.setCullHint(Spatial.CullHint.Never)
    sky.setModelBound(new BoundingSphere(Float.PositiveInfinity, Vector3f.ZERO))
    sky.rotate(-0.5f*math.Pi.toFloat, 0f, 0f)

    val skyMaterial = new Material(assetManager, "shaders/SimpleSky.j3md")
    sky.setMaterial(skyMaterial)

    //    val mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    //    mat.setColor("Color", ColorRGBA.Blue);
    //    sky.setMaterial(mat)

    // Center skybox on camera
    sky.addControl(new AbstractControl {
      def cloneForSpatial(spatial: Spatial) = null
      def controlRender(rm: RenderManager, vp: ViewPort) {}
      def controlUpdate(tpf: Float) {
        sky.setLocalTranslation(camera.getLocation)
      }
    })

    attachChild(sky)
  }




}
