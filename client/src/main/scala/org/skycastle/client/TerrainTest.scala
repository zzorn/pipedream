package org.skycastle.client

import com.jme3.app.SimpleApplication
import com.jme3.material.Material
import com.jme3.renderer.Camera
import com.jme3.terrain.heightmap.AbstractHeightMap
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator
import com.jme3.terrain.heightmap.HillHeightMap
import com.jme3.bullet.control.CharacterControl
import com.jme3.terrain.noise.fractal.FractalSum
import com.jme3.terrain.noise.filter.{IterativeFilter, SmoothFilter, OptimizedErode, PerturbFilter}
import com.jme3.app.state.ScreenshotAppState
import com.jme3.math.{Vector3f, ColorRGBA}
import com.jme3.terrain.geomipmap.grid.FractalTileLoader
import com.jme3.terrain.geomipmap.{TerrainGrid, TerrainLodControl, TerrainQuad}
import com.jme3.terrain.noise.modulator.NoiseModulator
import com.jme3.terrain.noise.ShaderUtils
import com.jme3.terrain.noise.basis.FilteredBasis

import com.jme3.terrain.heightmap.ImageBasedHeightMap
import com.jme3.texture.Texture
import com.jme3.texture.Texture.WrapMode
import java.util.ArrayList
import java.util.List
import com.jme3.water.WaterFilter
import com.jme3.post.FilterPostProcessor
import com.jme3.audio.AudioNode
import com.jme3.system.AppSettings
import com.jme3.util.SkyFactory


/**
 *
 */
object TerrainTest extends SimpleApplication  {

  private var terrain : TerrainQuad = null
  private var mat_terrain: Material = null

  private val grassScale = 64;
  private val dirtScale = 16;
  private val rockScale = 128;

  private var player3 : CharacterControl = null
  private var base  : FractalSum = null
  private var perturb : PerturbFilter  = null
  private var therm : OptimizedErode   = null
  private var smooth: SmoothFilter   = null
  private var iterate: IterativeFilter = null

  private var fpp: FilterPostProcessor = null
  private var water: WaterFilter = null
  private var lightDir = new Vector3f(-4.9f, -1.3f, 5.9f) // same as light source
  private var initialWaterHeight = 0.8f // choose a value for your scene

  def main(args: Array[String]) {
    val settings: AppSettings = new AppSettings(true)
    settings.setFrameRate(60)
    settings.setVSync(true)
    setSettings(settings);
    start()
  }

  @Override
  def simpleInitApp() {

    flyCam.setMoveSpeed(80)

    // TERRAIN TEXTURE material
    mat_terrain = new Material(this.assetManager, "Common/MatDefs/Terrain/HeightBasedTerrain.j3md");

    val state = new ScreenshotAppState();
    this.stateManager.attach(state);


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
    val grass = this.assetManager.loadTexture("Textures/Terrain/splat/grass.jpg")
    grass.setWrap(WrapMode.Repeat);
    this.mat_terrain.setTexture("region1ColorMap", grass);
    this.mat_terrain.setVector3("region1", new Vector3f(15, 200, this.grassScale));
  
    // DIRT texture
    val dirt = this.assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
    dirt.setWrap(WrapMode.Repeat);
    this.mat_terrain.setTexture("region2ColorMap", dirt);
    this.mat_terrain.setVector3("region2", new Vector3f(0, 20, this.dirtScale));
  
    // ROCK texture
    val rock = this.assetManager.loadTexture("Textures/Terrain/Rock2/rock.jpg");
    rock.setWrap(WrapMode.Repeat);
    this.mat_terrain.setTexture("region3ColorMap", rock);
    this.mat_terrain.setVector3("region3", new Vector3f(198, 260, this.rockScale));
  
    this.mat_terrain.setTexture("region4ColorMap", rock);
    this.mat_terrain.setVector3("region4", new Vector3f(198, 260, this.rockScale));
  
    this.mat_terrain.setTexture("slopeColorMap", rock);
    this.mat_terrain.setFloat("slopeTileFactor", 32);
  
    this.mat_terrain.setFloat("terrainSize", 513);
  
    this.base = new FractalSum();
    this.base.setRoughness(0.7f);
    this.base.setFrequency(1.0f);
    this.base.setAmplitude(1.0f);
    this.base.setLacunarity(2.12f);
    this.base.setOctaves(8);
    this.base.setScale(0.02125f);


/* TODO: This seems to be impossible to compile in scala....
    new NoiseModulator() {
      def value(in: Array[Float]): Float = {
        ShaderUtils.clamp(in(0) * 0.5f + 0.5f, 0, 1);
      }
    });
  */
    this.base.addModulator(new ClampingNoiseModulator())

    val ground = new FilteredBasis(this.base);
  
    this.perturb = new PerturbFilter();
    this.perturb.setMagnitude(0.119f);
  
    this.therm = new OptimizedErode();
    this.therm.setRadius(5);
    this.therm.setTalus(0.011f);
  
    this.smooth = new SmoothFilter();
    this.smooth.setRadius(1);
    this.smooth.setEffect(0.7f);
  
    this.iterate = new IterativeFilter();
    this.iterate.addPreFilter(this.perturb);
    this.iterate.addPostFilter(this.smooth);
    this.iterate.setFilter(this.therm);
    this.iterate.setIterations(1);
  
    ground.addPreFilter(this.iterate);

    val patchSize: Int = 32+1
    val maxVisibleSize: Int = 128+1
    this.terrain = new TerrainGrid("terrain", patchSize, maxVisibleSize, new FractalTileLoader(ground, 256f));
  
    this.terrain.setMaterial(this.mat_terrain);
    this.terrain.setLocalTranslation(0, -120f, 0);
    this.terrain.setLocalScale(1f, 1f, 1f);
    this.rootNode.attachChild(this.terrain);
  
    val control = new TerrainLodControl(this.terrain, this.getCamera());
    control.setLodCalculator(new DistanceLodCalculator(patchSize, 2.7f)); // patch size, and a multiplier
    this.terrain.addControl(control);

    // Water
    fpp = new FilterPostProcessor(assetManager);
    water = new WaterFilter(rootNode, lightDir);
    water.setWaterHeight(initialWaterHeight);
    fpp.addFilter(water);
    viewPort.addProcessor(fpp);

    // Sound
 /* Chops on ubuntu
    val waves = new AudioNode(assetManager, "Sound/Environment/Ocean Waves.ogg", false);
    waves.setLooping(true);
    audioRenderer.playSource(waves);
     */

    createSky()

    this.getCamera().setLocation(new Vector3f(0, 300, 0));
  
    this.viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
  
  }

  override def simpleUpdate(tpf: Float) {
  }


  private def createSky() {
    val west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
    val east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
    val north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
    val south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
    val up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
    val down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");

    val sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
    rootNode.attachChild(sky);
  }

}

