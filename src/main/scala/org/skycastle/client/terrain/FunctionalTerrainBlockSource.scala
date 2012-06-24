package org.skycastle.client.terrain

import com.jme3.material.Material
import definition.GroundDef
import com.jme3.asset.AssetManager

/**
 *
 */
class FunctionalTerrainBlockSource(terrainFunction: GroundDef,
                                   material: Material,
                                   sizeSettings: GroundSizeSettings) extends TerrainBlockSource {
  
  def createBlock(pos: BlockPos, assetManager: AssetManager): TerrainBlock = {

    new TerrainBlock(
      pos,
      material,
      terrainFunction,
      sizeSettings,
    assetManager = assetManager
    )
    
  }
  
}