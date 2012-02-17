package org.skycastle.client.terrain

import com.jme3.scene.Node
import com.jme3.terrain.{ProgressMonitor, Terrain}
import java.util.List
import com.jme3.terrain.geomipmap.lodcalc.LodCalculator
import com.jme3.math.{Vector2f, Vector3f}
import java.lang.Float
import com.jme3.material.Material
import com.jme3.asset.AssetManager

/**
 * A terrain based on the clipmap algorithm, to allow very long distance views at low cost.
 */
class ClipmapTerrain(terrainFunction: TerrainFunction,
                     material: Material,
                     assetManager: AssetManager,
                     smallestVertexDistance: Double = 1,
                     levelCount: Int = 4,
                     blockCellCount: Int = 32) extends Node /* with Terrain */ {
  // Preconditions
  require(levelCount >= 1, "There should be at least one level")
  require(blockCellCount >= 1, "There should be at least one cell along the sides of each block")
  require(smallestVertexDistance > 0)

  val sideBlockCount: Int = 8
  val holeBlockCount: Int = 4

  private var outermostLevel: LodLevel = null
  private var innermostLevel: LodLevel = null

  setupLodLevels(material)

  private def setupLodLevels(material: Material) {

    var levelScale = sideBlockCount / holeBlockCount
    var blockWorldSize = smallestVertexDistance * (blockCellCount);
    var level = new LodLevel(blockWorldSize, blockCellCount, sideBlockCount, holeBlockCount, terrainFunction, material)
    var prevLevel: LodLevel = level
    innermostLevel = level
    attachChild(innermostLevel.getSpatial(assetManager))

    // Loop from innermost to outermost level
    for (i <- 1 until levelCount) {
      blockWorldSize *= levelScale
      level = new LodLevel(
        blockWorldSize,
        blockCellCount,
        sideBlockCount,
        holeBlockCount,
        terrainFunction,
        material,
        innerLevel = prevLevel,
        xOffset = 0,
        zOffset = 0)
      if (prevLevel != null) prevLevel.outerLevel = level
      prevLevel = level
      attachChild(level.getSpatial(assetManager))
    }

    outermostLevel = level

  }

  // TODO: Add rendered meshes as child nodes


  def update(location: List[Vector3f], lodCalculator: LodCalculator) {
    // TODO: Anything to do?
  }

  def setLocked(locked: Boolean) {
    // TODO: Lock meshes?
  }

  def generateEntropy(monitor: ProgressMonitor) {
    // Anything we need to do?
    monitor.progressComplete()
  }

  // TODO: material
  def getMaterial = material
  def getMaterial(worldLocation: Vector3f) = material

  /*
  // Delegated height queries
  override def getHeight(xz: Vector2f) = terrainFunction.getHeight(xz)
  override def getNormal(xz: Vector2f) = terrainFunction.getNormal(xz)
  override def getHeightmapHeight(xz: Vector2f) = terrainFunction.getHeight(xz)
  override def setHeight(xz: java.util.List[Vector2f], height: java.util.List[Float]) {terrainFunction.setHeights(xz, height)}
  // TODO: For some unknown reason, these do not work:
  //  override def setHeight(xzCoordinate: Vector2f, height: Float): Unit = {terrainFunction.setHeight(xzCoordinate, height)}
  //  def adjustHeight(xzCoordinate: Vector2f, delta: Float) {terrainFunction.adjustHeight(xzCoordinate, delta)}
  override def adjustHeight(xz: java.util.List[Vector2f], height: java.util.List[Float]) {terrainFunction.adjustHeights(xz, height)}
  override def getHeightMap = null
  override def getMaxLod = 1
  override def getTerrainSize = blockCellCount // TODO: Is this ok?  What is it used for?
  override def getNumMajorSubdivisions = levelCount // TODO: Is this ok?  What is it used for?
  */
}
