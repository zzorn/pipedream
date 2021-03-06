package org.skycastle.client.engine

import com.jme3.app.SimpleApplication
import com.jme3.asset.plugins.FileLocator
import com.jme3.app.state.ScreenshotAppState
import org.skycastle.client.terrain.definition._
import org.skycastle.client.terrain.view.{SimpleGroundLodStrategy, Ground, FunctionalTerrainBlockSource}
import org.skycastle.client.sky.Sky
import com.jme3.math.{ColorRGBA, Vector3f}
import com.jme3.scene.{Node, Geometry}
import com.jme3.scene.shape.Box
import com.jme3.material.Material
import com.jme3.system.AppSettings
import com.jme3.texture.Texture
import org.skycastle.client.terrain.definition.MaterialLayer
import org.skycastle.client.terrain.definition.GroundMaterial
import org.skycastle.client.terrain.definition.MountainFun
import org.skycastle.client.terrain.definition.TurbulenceFun
import org.skycastle.client.terrain.definition.NoiseFun
import org.skycastle.client.terrain.definition.FoundationLayer
import org.skycastle.client.terrain.definition.GroundSizeSettings
import com.jme3.terrain.noise.basis.FilteredBasis
import com.jme3.terrain.noise.fractal.FractalSum
import org.skycastle.client.{ClientServices, ClampingNoiseModulator}
import com.jme3.terrain.noise.filter.{IterativeFilter, SmoothFilter, OptimizedErode, PerturbFilter}
import com.jme3.terrain.geomipmap.{TerrainLodControl, TerrainGrid, TerrainQuad}
import com.jme3.terrain.geomipmap.grid.FractalTileLoader
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator
import com.jme3.asset.AssetManager
import com.jme3.texture.Texture.WrapMode
import com.jme3.post.FilterPostProcessor
import com.jme3.water.WaterFilter
import java.util.logging.{Level, Logger}
import com.jme3.input.controls.{ActionListener, InputListener, KeyTrigger}
import com.jme3.input.KeyInput
import org.skycastle.client.region.{RegionListener, Region}
import org.skycastle.client.entity.Entity
import org.skycastle.utils.PropertyListener

import scala.collection.JavaConversions._

/**
 * JMonkey based 3D engine implementation.
 */
class EngineImpl(services: ClientServices) extends SimpleApplication with Engine {

  private val limitFps= true
  private val movementSpeed: Float = 400
  private val lightingOn = true

  private val startX = 0
  private val startZ = 0

  private val cameraStartOffset = new Vector3f(0, -100, -50)

  private val worldUp = new Vector3f(0, -1, 0)

  private val lightDir = new Vector3f(-4.9f, -1.3f, 5.9f)

  private var visibleRegions: Set[Region] = Set()
  private var currentRegion: Region = null

  private var focusedEntity: Entity = null

  private var updateCallbacks: List[(Float) => Unit] = Nil

  private var currentRegionNode = new Node()

  def setFocusEntity(entity: Entity) {
    focusedEntity = entity

    if (focusedEntity != null) {
      // Add listener that is notified when the entity changes regions
      focusedEntity.location.addListener('regionId, new PropertyListener {
        def apply(propertyName: Symbol, obj: Any, oldValue: Any, newValue: Any) {
          // Change active region
          setCurrentRegion(newValue.asInstanceOf[Symbol])
        }
      })
    }

  }

  def addUpdateCallback(callback: (Float) => Unit) {
    updateCallbacks ::= callback
  }

  def removeUpdateCallback(callback: (Float) => Unit) {
    updateCallbacks = updateCallbacks.filterNot(_ == callback)
  }

  def simpleInitApp() {

    assetManager.registerLocator("assets", classOf[FileLocator])

    flyCam.setMoveSpeed(movementSpeed)
    flyCam.setDragToRotate(true)
    getCamera.setFrustumFar(320000)

    // Allow screenshots
    this.stateManager.attach(new ScreenshotAppState())

    // Remap escape to close application
    inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT)
    inputManager.addMapping("SkycastleClientExit", new KeyTrigger(KeyInput.KEY_ESCAPE))
    inputManager.addListener(new ActionListener {
      def onAction(name: String, isPressed: Boolean, tpf: Float) {
        services.shutdown()
      }
    }, "SkycastleClientExit")

    /*
    // Terrain
    val terrainMaterial = createSimpleTerrainMaterial(getAssetManager)

    //    val block= new TerrainBlock(terrainMaterial, new TestTerrain(), 1000, 1000, 1000, 1000)
    //    val terrain = block.getGeometry(assetManager)
    //    val terrain = new ClipmapTerrain(new TestTerrain, terrainMaterial, getAssetManager, 0.25, 11, 32)
    val groundDef: GroundDef = createTestTerrain
    val sizeSettings: GroundSizeSettings = new GroundSizeSettings(32,1, 12)
    val terrain = new Ground(
      sizeSettings,
      groundDef,
      new FunctionalTerrainBlockSource(groundDef, terrainMaterial, sizeSettings),
      getCamera,
      new SimpleGroundLodStrategy(2, 0.25),
      assetManager)

    this.rootNode.attachChild(terrain)
    */

    /*
    // Sky
    val sky = new Sky(getCamera, assetManager)
    rootNode.attachChild(sky)
    sky.createLights(rootNode)

    // Start pos
    val startPos: Vector3f = new Vector3f(startX, groundDef.getHeight(startX, startZ, 1).toFloat + 2, startZ)
    this.getCamera.setLocation(startPos)

    // Refpoint
    val box = new Geometry("box", new Box(1, 1, 1))
    box.setLocalTranslation(startPos)
    val mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
    mat.setColor("Color", ColorRGBA.Red)
    box.setMaterial(mat)
    rootNode.attachChild(box)
    */

    // Entities
    this.rootNode.attachChild(currentRegionNode)


    // Get our current avatar entity, focus the camera on it, and listen for changes to the avatar entity
    setFocusedEntity(services.entityService.avatarEntity)
    services.entityService.addAvatarChangeListener(setFocusedEntity)



  }

  private def setFocusedEntity(entity: Entity) {
    if (entity != focusedEntity) {
      focusedEntity = entity
      if (focusedEntity != null && focusedEntity.location != null) {

        // Change to the region the entity is in
        setCurrentRegion(focusedEntity.location.regionId)

        // Move camera aside a bit from the entity
        val camPos = new Vector3f(focusedEntity.location.pos)
        camPos.addLocal(cameraStartOffset)
        cam.setLocation(camPos)

        // Look at entity
        cam.lookAt(focusedEntity.location.pos, worldUp)

        // Follow entity
        // TODO

      }
    }
  }

  private val regionListener = new RegionListener {
    def onEntityAdded(entity: Entity) {
      currentRegionNode.attachChild(entity.appearance.getSpatial(services))
    }
    def onEntityRemoved(entity: Entity) {
      currentRegionNode.detachChild(entity.appearance.getSpatial(services))
    }
    def onAllEntitiesRemoved() {
      currentRegionNode.detachAllChildren()
    }
  }

  private def setCurrentRegion(regionId: Symbol) {
    val region = services.regionService.getRegion(regionId)
    if (region != currentRegion) {
      // Hide entities from the previous region
      if (currentRegion != null) {
        currentRegion.removeRegionListener(regionListener)
        currentRegionNode.detachAllChildren()
      }

      currentRegion = region

      if (currentRegion != null) {
        // Show all entities in the region
        currentRegion.entities foreach {e =>
          currentRegionNode.attachChild(e.appearance.getSpatial(services))
        }

        // Listen to any added ore removed entities in the region
        currentRegion.addRegionListener(regionListener)
      }
    }
  }


  override def startup() {
    // Tune jme logging level
    Logger.getLogger("com.jme3").setLevel(Level.WARNING)

    // Settings
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

    // Start the 3D engine
    start()
  }

  override def shutdown() {
    // Stop the engine, wait until it is fully destroyed.

    // stop(true) // TODO: Stays hanging open, never exists?
    stop(false)
  }




  private def createTestTerrain: GroundDef = {
    def createMaterial(name: Symbol,  textureFile: String): GroundMaterial = {
      val texture: Texture = assetManager.loadTexture(textureFile)
      texture.setWrap(Texture.WrapMode.Repeat)
      new GroundMaterial(name, texture)
    }

    val bedrock = createMaterial('bedrock, "textures/grey_rock-diffuse.png")
    val stone   = createMaterial('stone, "textures/regolith.png")
    val grass   = createMaterial('grass, "textures/twisty_grass.png")
    val sand    = createMaterial('sand,  "textures/sand.png")

    val groundDef: GroundDef = new GroundDef()
    groundDef.addLayer(new FoundationLayer(-1.0, bedrock,
      MountainFun(size = 1000000, altitude = 10000, sharpness = 4, offset=0.98213)
        - MountainFun(size = 50000, altitude = 7000, sharpness = 3, offset=0.9843211233)
        - NoiseFun(sizeScale = 10000, seed=342123, amplitude = 1000)))
    groundDef.addLayer(new MaterialLayer(0.0, stone,
      //NoiseFun(sizeScale = 1000, seed=2356451, amplitude = 100)
      MountainFun(size = 100000, altitude = 1000, sharpness = 4, offset=0.98213)
        + MountainFun(size = 1000, altitude = 100, sharpness = 2)
        + MountainFun(size = 10000, altitude = 1000, sharpness = 4, offset = 1.0123)  ))
    groundDef.addLayer(new MaterialLayer(0.5, sand,
      //NoiseFun(sizeScale = 200, seed=453242, amplitude = 10) /*
      TurbulenceFun(2, sizeX = 2000, sizeZ= 2000, amplitude = 20, offsetZ = 34123.123)
        + NoiseFun(sizeX= 21234, sizeZ= 12433, amplitude= 10, offsetZ = 32335.12)
        + NoiseFun(sizeX= 13412, sizeZ= 42313,  amplitude= 5, offsetZ = 335.12)
        + MountainFun(size = 5100, altitude = 10,  sharpness = 2, offset = 1.123)
        + NoiseFun(offsetX = 764.12, amplitude = 0.2, sizeX = 1.3f, sizeZ = 1)))
    groundDef.addLayer(new MaterialLayer(1.0, grass,
      //NoiseFun(sizeScale = 50, seed=123545, amplitude = 1) /*
      TurbulenceFun(sizeX = 1000, sizeZ = 1000, amplitude = 2, offsetZ = 5423.123)
        + NoiseFun(offsetX = 1235.12, amplitude = 2, sizeX = 100, sizeZ = 200)
        + NoiseFun(offsetX = 2335.12, amplitude = 0.4, sizeX = 2, sizeZ = 2)
        - ConstFun(0.4)  ))
    groundDef
  }


  private def createGround: FilteredBasis = {
    val base = new FractalSum();
    base.setRoughness(0.7f);
    base.setFrequency(1.0f);
    base.setAmplitude(1.0f);
    base.setLacunarity(2.12f);
    base.setOctaves(8);
    base.setScale(0.02125f);


    /* TODO: This seems to be impossible to compile in scala....
      new NoiseModulator() {
        def value(in: Array[Float]): Float = {
          ShaderUtils.clamp(in(0) * 0.5f + 0.5f, 0, 1);
        }
      });
    */
    base.addModulator(new ClampingNoiseModulator())

    val ground = new FilteredBasis(base);

    val perturb = new PerturbFilter();
    perturb.setMagnitude(0.119f);

    val therm = new OptimizedErode();
    therm.setRadius(5);
    therm.setTalus(0.011f);

    val smooth = new SmoothFilter();
    smooth.setRadius(1);
    smooth.setEffect(0.7f);

    val iterate = new IterativeFilter();
    iterate.addPreFilter(perturb);
    iterate.addPostFilter(smooth);
    iterate.setFilter(therm);
    iterate.setIterations(1);

    ground.addPreFilter(iterate);
    ground
  }

  private def createTerrain(ground: FilteredBasis, material: Material): TerrainQuad = {
    val patchSize: Int = 32 + 1
    val maxVisibleSize: Int = 128 + 1
    val terrain = new TerrainGrid("terrain", patchSize, maxVisibleSize, new FractalTileLoader(ground, 256f));

    terrain.setMaterial(material);
    terrain.setLocalTranslation(0, -120f, 0);
    terrain.setLocalScale(10f, 1f, 10f);

    val control = new TerrainLodControl(terrain, this.getCamera);
    control.setLodCalculator(new DistanceLodCalculator(patchSize, 2.7f)); // patch size, and a multiplier
    terrain.addControl(control);
    terrain
  }

  private def createSimpleTerrainMaterial(assetManager: AssetManager): Material = {
    //val texture1: Texture = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg")
    val texture1: Texture = assetManager.loadTexture("textures/twisty_grass.png")
    val texture2: Texture = assetManager.loadTexture("textures/regolith.png")
    val texture3: Texture = assetManager.loadTexture("textures/sand.png")
    texture1.setWrap(Texture.WrapMode.Repeat)
    texture2.setWrap(Texture.WrapMode.Repeat)
    texture3.setWrap(Texture.WrapMode.Repeat)

    var mat_terrain: Material = null
    if (lightingOn) {
      //mat_terrain = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md")
      //mat_terrain.setTexture("DiffuseMap", texture1);
      mat_terrain = new Material(assetManager, "shaders/GroundTest.j3md")
      mat_terrain.setTexture("Ecotope1Map", texture1)
      mat_terrain.setTexture("Ecotope2Map", texture2)
      mat_terrain.setTexture("Ecotope3Map", texture3)
    }
    else {
      mat_terrain = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
      mat_terrain.setTexture("ColorMap", texture1)
    }

    mat_terrain
  }

  private def createWireframeMaterial(assetManager: AssetManager): Material =  {
    val mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
    mat.setColor("Color", ColorRGBA.Green)

    // Wireframe mode
    mat.getAdditionalRenderState.setWireframe(true)
    mat
  }

  private def createTerrainMaterial(assetManager: AssetManager, grassScale: Float, dirtScale: Float, rockScale: Float): Material = {
    // TERRAIN TEXTURE material
    val mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/HeightBasedTerrain.j3md")


    // Parameters to material:
    // regionXColorMap: X = 1..4 the texture that should be applied to state X
    // regionX: a Vector3f containing the following information:
    //      regionX.x: the start height of the region
    //      regionX.y: the end height of the region
    //      regionX.z: the texture scale for the region
    //  it might not be the most elegant way for storing these 3 values, but it packs the data nicely :)
    // slopeColorMap: the texture to be used for cliffs, and steep mountain sites
    // slopeTileFactor: the texture scale for slopes
    // terrainSize: the total size of the terrain (used for scaling the texture)
    // GRASS texture
    val grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg")
    grass.setWrap(WrapMode.Repeat)
    mat_terrain.setTexture("region1ColorMap", grass)
    mat_terrain.setVector3("region1", new Vector3f(15, 200, grassScale))

    // DIRT texture
    val dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg")
    dirt.setWrap(WrapMode.Repeat)
    mat_terrain.setTexture("region2ColorMap", dirt)
    mat_terrain.setVector3("region2", new Vector3f(0, 20, dirtScale))

    // ROCK texture
    val rock = assetManager.loadTexture("Textures/Terrain/Rock2/rock.jpg")
    rock.setWrap(WrapMode.Repeat)
    mat_terrain.setTexture("region3ColorMap", rock)
    mat_terrain.setVector3("region3", new Vector3f(198, 260, rockScale))

    mat_terrain.setTexture("region4ColorMap", rock)
    mat_terrain.setVector3("region4", new Vector3f(198, 260, rockScale))

    mat_terrain.setTexture("slopeColorMap", rock)
    mat_terrain.setFloat("slopeTileFactor", 32)

    mat_terrain.setFloat("terrainSize", 513)
    mat_terrain
  }

  private def createWater(assetManager: AssetManager, rootNode: Node, lightDir: Vector3f, initialWaterHeight: Float): FilterPostProcessor = {
    val water = new WaterFilter(rootNode, lightDir)
    water.setWaterHeight(initialWaterHeight)
    //water.setSpeed(0.6f)
    //water.setWaveScale(0.001f)
    //water.setDeepWaterColor(new ColorRGBA(0.0001f, 0.00196f, 0.145f, 1.0f))
    //water.setWaterColor(new ColorRGBA(0.1f, 0.11f, 0.145f, 1.0f))
    //water.setWaterTransparency(0.2f)
    //water.setUseFoam(false)
    water.setFoamIntensity(1f)

    val fpp = new FilterPostProcessor(assetManager)
    fpp.addFilter(water)
    fpp
  }

  override def simpleUpdate(tpf: Float) {
    updateCallbacks foreach {cb => cb(tpf)}
  }

}
