package org.skycastle.client.terrain.view

import com.jme3.material.Material
import definition.{GroundSizeSettings, GroundDef}
import com.jme3.asset.AssetManager
import org.skycastle.client.terrain.definition.{GroundSizeSettings, GroundDef}
import org.skycastle.client.terrain.view.TerrainBlockSource
import org.skycastle.client.terrain.{BlockPos, TerrainBlockSource}

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