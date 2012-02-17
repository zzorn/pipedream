package org.skycastle.client.terrain

import com.jme3.asset.AssetManager
import com.jme3.material.Material
import com.jme3.scene.{Node, Spatial}

/**
 * A mesh at a specific level of detail, optionally with a hole in the middle for an inner level,
 * and optionally surrounded by an outer level.
 */
class LodLevel(blockWorldSize: Double,
               blockCellCount: Int, 
               sideBlockCount: Int,
               holeBlockCount: Int,
               terrainFunction: TerrainFunction,
               material: Material,
               var xOffset: Double = 0,
               var zOffset: Double = 0,
               var innerLevel: LodLevel = null,
               var outerLevel: LodLevel = null) {

  private var node: Node = null
  
  private val blocks: Array[TerrainBlock] = new Array[TerrainBlock](sideBlockCount * sideBlockCount)

  var innerLevelXOffs: Int = (sideBlockCount - holeBlockCount)/2
  var innerLevelZOffs: Int = (sideBlockCount - holeBlockCount)/2

  def getSpatial(assetManager: AssetManager): Spatial = {
    if (node == null) node = initBlocks(assetManager)
    node
  }

  def hasHole = innerLevel != null

  private def initBlocks(assetManager: AssetManager): Node = {
    val node = new Node("LodLevel")

    var i = 0
    var xBlock = 0
    var zBlock = 0
    while (zBlock < sideBlockCount ) {
      xBlock = 0
      while (xBlock < sideBlockCount ) {

        if (isInHole(xBlock, zBlock)) {
          blocks(i) = null
        }
        else {
          val block = createBlock(assetManager,
            xBlock * blockWorldSize + xOffset - sideBlockCount*blockWorldSize / 2.0,
            zBlock * blockWorldSize + zOffset - sideBlockCount*blockWorldSize / 2.0)
          blocks(i) = block
          node.attachChild(block.getGeometry(assetManager))
        }
        
        xBlock += 1
        i += 1
      }
      zBlock += 1
    }

    node
  }

  private def isInHole(xBlock: Int, zBlock: Int): Boolean = {
    hasHole &&
      xBlock >= innerLevelXOffs && xBlock < innerLevelXOffs + holeBlockCount &&
      zBlock >= innerLevelZOffs && zBlock < innerLevelZOffs + holeBlockCount
  }
  
 
  private def createBlock(assetManager: AssetManager, xOffs: Double, zOffs: Double): TerrainBlock = {
    val marginSize = 2
    val cellWorldSize: Double = blockWorldSize / blockCellCount
    val vertexCount: Int = blockCellCount + 1
    new TerrainBlock(
      material,
      terrainFunction,
      vertexCount + marginSize,
      vertexCount + marginSize,
      blockWorldSize + cellWorldSize * marginSize,
      blockWorldSize + cellWorldSize * marginSize,
      xOffs - cellWorldSize * marginSize / 2,
      zOffs - cellWorldSize * marginSize / 2)
  }


}