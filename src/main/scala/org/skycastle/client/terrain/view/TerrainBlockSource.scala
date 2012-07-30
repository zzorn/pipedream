package org.skycastle.client.terrain.view

import com.jme3.asset.AssetManager
import org.skycastle.client.terrain.BlockPos

/**
 * Creates terrain blocks for given locations at given level of detail.
 */
trait TerrainBlockSource {

  def createBlock(pos: BlockPos, assetManager: AssetManager): TerrainBlock

}