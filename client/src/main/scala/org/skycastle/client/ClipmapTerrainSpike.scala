package org.skycastle.client

import com.jme3.app.SimpleApplication
import com.jme3.material.Material
import com.jme3.bullet.control.CharacterControl
import com.jme3.terrain.noise.fractal.FractalSum
import com.jme3.terrain.noise.filter.{IterativeFilter, SmoothFilter, OptimizedErode, PerturbFilter}
import com.jme3.post.FilterPostProcessor
import com.jme3.water.WaterFilter
import com.jme3.system.AppSettings
import com.jme3.app.Application._
import com.jme3.app.SimpleApplication._
import com.jme3.app.state.ScreenshotAppState
import com.jme3.texture.Texture.WrapMode
import com.jme3.terrain.noise.basis.FilteredBasis
import com.jme3.terrain.geomipmap.grid.FractalTileLoader
import com.jme3.terrain.geomipmap.{TerrainLodControl, TerrainGrid, TerrainQuad}
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator
import com.jme3.audio.AudioNode
import com.jme3.util.SkyFactory
import com.jme3.asset.AssetManager
import com.jme3.scene.{Geometry, Node, Spatial}
import com.jme3.scene.shape.Box
import com.jme3.post.filters.FogFilter
import terrain._
import com.jme3.texture.Texture
import com.jme3.light.{AmbientLight, DirectionalLight}
import com.jme3.math.{ColorRGBA, Vector3f}
import com.jme3.asset.plugins.FileLocator

/**
 *
 */
object ClipmapTerrainSpike extends SimpleApplication  {

  private val waterOn = false
  private val wireframe = false
  private val limitFps= false
  private val lightingOn = !waterOn

  private val startX = 33010
  private val startZ = -100000

  private val lightDir = new Vector3f(-4.9f, -1.3f, 5.9f)

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
    start()
  }

  @Override
  def simpleInitApp() {

    assetManager.registerLocator("assets", classOf[FileLocator])

    flyCam.setMoveSpeed(200)
    getCamera.setFrustumFar(320000)

    // Allow screenshots
    this.stateManager.attach(new ScreenshotAppState());

    // Terrain
/*
    val terrainMaterial = createTerrainMaterial(assetManager, grassScale, dirtScale, rockScale)
    val groundFunction = createGround
    val terrain = createTerrain(groundFunction, terrainMaterial)
     */
    val terrainMaterial = if (wireframe) createWireframeMaterial(assetManager) else createSimpleTerrainMaterial(getAssetManager)

//    val block= new TerrainBlock(terrainMaterial, new TestTerrain(), 1000, 1000, 1000, 1000)
//    val terrain = block.getGeometry(assetManager)
//    val terrain = new ClipmapTerrain(new TestTerrain, terrainMaterial, getAssetManager, 0.25, 11, 32)
    val terrainFunction: TestTerrain = new TestTerrain
    val sizeSettings: GroundSizeSettings = new GroundSizeSettings(32,1, 12)
    val terrain = new Ground(
      sizeSettings,
      terrainFunction,
      new FunctionalTerrainBlockSource(terrainFunction, terrainMaterial, sizeSettings),
      getCamera,
      new SimpleGroundLodStrategy(2, 0.25))

    this.rootNode.attachChild(terrain);

    // Refpoint
    val box = new Geometry("box", new Box(1, 1, 1))
    val mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", ColorRGBA.Red);
    box.setMaterial(mat)
    rootNode.attachChild(box)

    // Fog
    if(lightingOn) viewPort.addProcessor(createFog(assetManager))

    // Water
    if (waterOn) viewPort.addProcessor(createWater(assetManager, rootNode, lightDir, 0));



    // Sound
    // Chops on ubuntu 11.10
    /*
    val waves = new AudioNode(assetManager, "Sound/Environment/Ocean Waves.ogg", false);
    waves.setLooping(true);
    audioRenderer.playSource(waves);
    */

    // Sky
    this.viewPort.setBackgroundColor(new ColorRGBA(0.3f, 0.3f, 0.3f, 1f));
//    this.viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
//    rootNode.attachChild(createSky(assetManager))

    createLight(rootNode)

    // Start pos
    this.getCamera().setLocation(new Vector3f(startX, terrainFunction.getHeight(startX,startZ).toFloat +2, startZ));

  }

  def createLight(node: Node) {
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

  def createFog(manager: AssetManager): FilterPostProcessor = {
    val fpp = new FilterPostProcessor(manager);
    //fpp.setNumSamples(4);
    val fog = new FogFilter();
    fog.setFogColor(new ColorRGBA(0.3f, 0.5f, 0.8f, 1.0f));
    fog.setFogDistance(50000);
    fog.setFogDensity(1.5f);
    fpp.addFilter(fog);
    fpp
  }

  def createGround: FilteredBasis = {
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

  def createTerrain(ground: FilteredBasis, material: Material): TerrainQuad = {
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

  def createSimpleTerrainMaterial(assetManager: AssetManager): Material = {
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
      mat_terrain.setTexture("Ecotope1Map", texture1);
      mat_terrain.setTexture("Ecotope2Map", texture2);
      mat_terrain.setTexture("Ecotope3Map", texture3);
    }
    else {
      mat_terrain = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
      mat_terrain.setTexture("ColorMap", texture1);
    }

    mat_terrain
  }

  def createWireframeMaterial(assetManager: AssetManager): Material =  {
    val mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", ColorRGBA.Green);

    // Wireframe mode
    mat.getAdditionalRenderState.setWireframe(true);
    mat
  }

  def createTerrainMaterial(assetManager: AssetManager, grassScale: Float, dirtScale: Float, rockScale: Float): Material = {
    // TERRAIN TEXTURE material
    val mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/HeightBasedTerrain.j3md");


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
    grass.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("region1ColorMap", grass);
    mat_terrain.setVector3("region1", new Vector3f(15, 200, grassScale));

    // DIRT texture
    val dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
    dirt.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("region2ColorMap", dirt);
    mat_terrain.setVector3("region2", new Vector3f(0, 20, dirtScale));

    // ROCK texture
    val rock = assetManager.loadTexture("Textures/Terrain/Rock2/rock.jpg");
    rock.setWrap(WrapMode.Repeat);
    mat_terrain.setTexture("region3ColorMap", rock);
    mat_terrain.setVector3("region3", new Vector3f(198, 260, rockScale));

    mat_terrain.setTexture("region4ColorMap", rock);
    mat_terrain.setVector3("region4", new Vector3f(198, 260, rockScale));

    mat_terrain.setTexture("slopeColorMap", rock);
    mat_terrain.setFloat("slopeTileFactor", 32);

    mat_terrain.setFloat("terrainSize", 513);
    mat_terrain
  }

  def createWater(assetManager: AssetManager, rootNode: Node, lightDir: Vector3f, initialWaterHeight: Float): FilterPostProcessor = {
    val water = new WaterFilter(rootNode, lightDir);
    water.setWaterHeight(initialWaterHeight);
    //water.setSpeed(0.6f)
    //water.setWaveScale(0.001f)
    //water.setDeepWaterColor(new ColorRGBA(0.0001f, 0.00196f, 0.145f, 1.0f))
    //water.setWaterColor(new ColorRGBA(0.1f, 0.11f, 0.145f, 1.0f))
    //water.setWaterTransparency(0.2f)
    //water.setUseFoam(false)
    water.setFoamIntensity(1f)

    val fpp = new FilterPostProcessor(assetManager);
    fpp.addFilter(water);
    fpp
  }

  override def simpleUpdate(tpf: Float) {
  }


  private def createSky(manager: AssetManager): Spatial = {
    val west = manager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
    val east = manager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
    val north = manager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
    val south = manager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
    val up = manager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
    val down = manager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");

    val sky = SkyFactory.createSky(manager, west, east, north, south, up, down);
    sky
  }

}

