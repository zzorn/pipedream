package org.skycastle.client.terrain

import com.jme3.material.Material

/**
 *
 */
class FunctionalTerrainBlockSource(terrainFunction: Terrain,
                                   material: Material,
                                   sizeSettings: GroundSizeSettings) extends TerrainBlockSource {
  
  def createBlock(pos: BlockPos): TerrainBlock = {

    new TerrainBlock(
      pos,
      material,
      terrainFunction,
      sizeSettings
    )
    
  }
  
}