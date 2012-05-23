package org.skycastle.client.shapes

import java.io.File

import com.jme3.app.SimpleApplication
import com.jme3.system.AppSettings
import com.jme3.asset.plugins.FileLocator
import com.jme3.material.Material
import com.jme3.math.{ColorRGBA, Vector3f}
import com.jme3.asset.AssetManager
import com.jme3.scene.{Spatial, Geometry}
import com.jme3.scene.shape.Box
import com.jme3.app.state.ScreenshotAppState
import com.jme3.input.controls.KeyTrigger
import com.jme3.input.KeyInput

import org.skycastle.client.sky.Sky
import org.skycastle.utils.{Logging, FileChangeMonitor}

import components.{Tome, Model}
import loader.ModelLoader

/**
 * Live preview of procedural shape defined in a config file.
 * Support for updating the shape when the config file is changed.
 */
object ShapeViewer extends SimpleApplication with Logging {

  private val wireframe = false
  private val limitFps= true

  private val startX = 0
  private val startY = 5
  private val startZ = 20

  private var shape: Spatial = null

  private var model: Model = null
  
  private var tomes: Map[String, Tome] = Map()

  private var chaseCamera: RotationCameraControl = null

  private var modelLoader: ModelLoader = null
  private val modelSource: File = new File("assets/shapes/TestShape.yaml")
  private var modelSourcesToRead: File = null
  private var fileChangeMonitor:  FileChangeMonitor = null
  
  def main(args: Array[String]) {
    Logging.initializeLogging()

    modelLoader = new ModelLoader()

    val settings: AppSettings = new AppSettings(true)
    if (limitFps) {
      settings.setFrameRate(60)
      settings.setVSync(true)
    }
    else {
      settings.setFrameRate(-1)
      settings.setVSync(false)
    }
    setSettings(settings)

    setPauseOnLostFocus(false)

    // Start polling
    fileChangeMonitor = new FileChangeMonitor(modelSource, setFileToRead _)
    fileChangeMonitor.start()

    start()
  }

  private def setFileToRead(file: File) {
    synchronized {
      modelSourcesToRead = file
    }
  }

  private def getFileToRead(): File = {
    synchronized {
      val file = modelSourcesToRead
      modelSourcesToRead = null
      file
    }
  }

  def updateModel(modelFile: File) {
    if (shape != null) {
      shape.removeControl(chaseCamera)
      rootNode.detachChild(shape)
    }

    shape = createModel(assetManager,modelFile);

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

    // Sky
    val sky = new Sky(getCamera, assetManager)
    rootNode.attachChild(sky)
    sky.createLights(rootNode)

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
    val file = getFileToRead()
    if (file != null) {
      updateModel(file)
    }
    
    
  }

  private def createModel(assetManager: AssetManager, modelFile: File): Spatial = {
    model = loadModel(modelFile)
    if (model != null) model.createSpatial(assetManager)
    else null
  }

  private def loadModel(modelFile: File): Model = {
    val newTomes: Map[String, Tome] = modelLoader.loadModels(modelFile)
    tomes ++= newTomes
    
    if (newTomes.isEmpty) null
    else newTomes.head._2.model
    
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
