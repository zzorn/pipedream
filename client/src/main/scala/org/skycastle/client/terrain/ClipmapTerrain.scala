package org.skycastle.client.terrain

import com.jme3.scene.Node
import com.jme3.terrain.{ProgressMonitor, Terrain}
import java.util.List
import com.jme3.terrain.geomipmap.lodcalc.LodCalculator
import com.jme3.math.{Vector2f, Vector3f}
import java.lang.Float

/**
 * A terrain based on the clipmap algorithm, to allow very long distance views at low cost.
 */
class ClipmapTerrain(terrainFunction: TerrainFunction,
                     smallestVertexDistance: Float = 1,
                     levelCount: Int = 4,
                     levelVertexSideCount: Int = 32,
                     innerLevelSize: Int = 16,
                     vertexCountScale: Int = 2) extends Node with Terrain {
  // Preconditions
  require(levelCount >= 1, "There should be at least one level")
  require(levelVertexSideCount >= 5, "There should be at least five vertexes along the sides of each level")
  require(innerLevelSize >= 1)
  require(innerLevelSize < levelVertexSideCount)
  require(smallestVertexDistance > 0)
  require(vertexCountScale >= 2)

  private var outermostLevel: LodLevel = null
  private var innermostLevel: LodLevel = null

  setupLodLevels()

  private def setupLodLevels() {

    var levelVertexDistance = smallestVertexDistance;
    var level = new LodLevel(levelVertexDistance, levelVertexSideCount)
    var prevLevel: LodLevel = null
    innermostLevel = level

    // Loop from innermost to outermost level
    for (i <- 1 to levelCount) {
      levelVertexDistance *= vertexCountScale
      level = new LodLevel(levelVertexDistance, levelVertexSideCount, prevLevel)
      if (prevLevel != null) prevLevel.outerLevel = level
      prevLevel = level
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
  def getMaterial = null
  def getMaterial(worldLocation: Vector3f) = null

  
  // Delegated height queries
  def getHeight(xz: Vector2f) = terrainFunction.getHeight(xz)
  def getNormal(xz: Vector2f) = terrainFunction.getNormal(xz)
  def getHeightmapHeight(xz: Vector2f) = terrainFunction.getHeight(xz)
  def setHeight(xzCoordinate: Vector2f, height: Float) {terrainFunction.setHeight(xzCoordinate, height)}
  def setHeight(xz: List[Vector2f], height: List[Float]) {terrainFunction.setHeights(xz, height)}
  def adjustHeight(xzCoordinate: Vector2f, delta: Float) {terrainFunction.adjustHeight(xzCoordinate, delta)}
  def adjustHeight(xz: List[Vector2f], height: List[Float]) {terrainFunction.adjustHeights(xz, height)}
  def getHeightMap = null
  def getMaxLod = 1
  def getTerrainSize = levelVertexSideCount // TODO: Is this ok?  What is it used for?
  def getNumMajorSubdivisions = levelCount // TODO: Is this ok?  What is it used for?
}
