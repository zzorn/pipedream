package org.skycastle.client.shapes

import com.jme3.app.SimpleApplication
import com.jme3.system.AppSettings
import com.jme3.app.SimpleApplication._
import com.jme3.asset.plugins.FileLocator
import components.{Tome, Cube, Model}
import loader.ModelLoader
import org.skycastle.client.terrain.definition.GroundDef
import org.skycastle.client.terrain.{SimpleGroundLodStrategy, FunctionalTerrainBlockSource, Ground, GroundSizeSettings}
import com.jme3.material.Material
import com.jme3.math.{ColorRGBA, Vector3f}
import com.jme3.light.DirectionalLight
import com.jme3.asset.AssetManager
import com.jme3.scene.{Spatial, Node, Geometry}
import com.jme3.scene.shape.{Sphere, Box}
import com.jme3.renderer.queue.RenderQueue.Bucket
import com.jme3.bounding.BoundingSphere
import com.jme3.scene.control.AbstractControl
import com.jme3.renderer.{RenderManager, ViewPort}
import org.skycastle.client.sky.Sky
import com.jme3.app.state.{AbstractAppState, ScreenshotAppState}
import com.jme3.app.Application._
import com.jme3.input.controls.{ActionListener, KeyTrigger}
import com.jme3.input.{ChaseCamera, KeyInput}
import java.io.{FilenameFilter, File}
import org.sadun.util.polling.{FileFoundEvent, BasePollManager, DirectoryPoller}

/**
 * Live preview of procedural shape defined in a config file.
 * Support for updating the shape when the config file is changed.
 */
object ShapeViewer extends SimpleApplication  {

  private val wireframe = false
  private val limitFps= true

  private val startX = 0
  private val startY = 5
  private val startZ = 20

  private var shape: Spatial = null

  private var model: Model = null
  
  private var tomes: Map[String, Tome] = Map()

  private var chaseCamera: RotationCameraControl = null

  private val modelLoader = new ModelLoader()
  private var modelSourceDir: File = new File("assets/shapes/")
  private var modelSourcesToRead: List[File] = Nil
  private var poller: DirectoryPoller = null

  
  def main(args: Array[String]) {
    val settings: AppSettings = new AppSettings(true)
    if (limitFps) {
      settings.setFrameRate(60)
      settings.setVSync(true)
    }
    else {
      settings.setFrameRate(-1)
      settings.setVSync(false)
    }
    setSettings(settings);

    setPauseOnLostFocus(false)

    // Start polling
    poller = new DirectoryPoller(modelSourceDir, new FilenameFilter {
      def accept(dir: File, name: String): Boolean = name.endsWith(".yaml")
    }, true)

    poller.addPollManager(new BasePollManager {

      override def fileFound(evt: FileFoundEvent) {
        println("Change " + evt.getFile)
        addFileToCheck(evt.getFile)
      }
    })
    poller.setPollInterval(500)
    poller.setSendSingleFileEvent(true)
    poller.setDaemon(true)
    poller.start()

    start()
  }

  private def addFileToCheck(file: File) {
    synchronized {
      modelSourcesToRead ::= file
    }
  }

  private def getFilesToCheck(): List[File] = {
    synchronized {
      val files = modelSourcesToRead
      modelSourcesToRead = Nil
      files
    }
  }

  def updateModel(modelFiles: List[File]) {
    if (shape != null) {
      shape.removeControl(chaseCamera)
      rootNode.detachChild(shape)
    }

    shape = createModel(assetManager,modelFiles);

    if (shape != null) {
      rootNode.attachChild(shape)
      shape.addControl(chaseCamera)
    }
  }

  def simpleInitApp() {

    assetManager.registerLocator("assets", classOf[FileLocator])

    // getCamera.setFrustumFar(320000)

    // Setup camera control
    chaseCamera = new RotationCameraControl(getCamera, inputManager)
    chaseCamera.setDragToRotate(true)


    // Allow screenshots
    stateManager.attach(new ScreenshotAppState());

    // Background
    viewPort.setBackgroundColor(new ColorRGBA(0.3f, 0.3f, 0.3f, 1f));

    /*
    // Sky
    val sky = new Sky(getCamera, assetManager)
    rootNode.attachChild(sky)
    sky.createLights(rootNode)
    */

    // Start pos
    val startPos: Vector3f = new Vector3f(startX, startY, startZ)
    this.getCamera.setLocation(startPos);

    // Load model
    //updateModel()

    // TODO: Wireframe support
    // val terrainMaterial = if (wireframe) createWireframeMaterial(assetManager) else createSimpleTerrainMaterial(getAssetManager)


    setupKeys()


  }


  override def simpleUpdate(tpf: Float) {
    val files: List[File] = getFilesToCheck()
    if (!files.isEmpty) {
      updateModel(files)
    }
    
    
  }

  private def createModel(assetManager: AssetManager, modelFiles: List[File]): Spatial = {
    model = loadModel(modelFiles)
    model.createSpatial(assetManager)
  }

  private def loadModel(modelFiles: List[File]): Model = {
    val newTomes: Map[String, Tome] = modelLoader.loadModels(modelFiles)
    tomes ++= newTomes
    newTomes.head._2.model
    
    // TODO: Detect removed tomes

    //new Cube()
  }

  private def setupKeys() {
    inputManager.addMapping("ReloadModel", new KeyTrigger(KeyInput.KEY_SPACE));

    /*
    inputManager.addListener(new ActionListener {
      def onAction(name: String, isPressed: Boolean, tpf: Float) { if (!isPressed) updateModel() }
    }, "ReloadModel")
    */
  }

  
  private def makeTestBox(pos: Vector3f = Vector3f.ZERO): Geometry = {
    val box = new Geometry("box", new Box(1, 1, 1))
    box.setLocalTranslation(pos)
    val mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", ColorRGBA.randomColor());
    box.setMaterial(mat)
    box
  }
  


}
