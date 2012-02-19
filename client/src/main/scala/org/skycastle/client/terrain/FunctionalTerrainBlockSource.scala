package org.skycastle.client.terrain

import com.jme3.material.Material

/**
 *
 */
class FunctionalTerrainBlockSource(terrainFunction: TerrainFunction,
                                   material: Material,
                                   cellCount: Int,
                                   finestLodCellWorldSize: Double
                                    ) extends TerrainBlockSource {
  
  def createBlock(pos: BlockPos): TerrainBlock = {

    // TODO: Do properly, mostly inside terrain block
    val vertexCount = cellCount + 1 + 2
    val worldSize = finestLodCellWorldSize * cellCount * Math.pow(2, pos.lodLevel)
    val worldX = pos.xPos * worldSize
    val worldZ = pos.xPos * worldSize
    new TerrainBlock(
      pos,
      material,
      terrainFunction,
      vertexCount, vertexCount,
      worldSize, worldSize,
      worldX, worldZ
    )
    
  }
  
}