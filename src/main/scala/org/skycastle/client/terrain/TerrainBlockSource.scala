package org.skycastle.client.terrain

import com.jme3.asset.AssetManager

/**
 * Creates terrain blocks for given locations at given level of detail.
 */
trait TerrainBlockSource {

  def createBlock(pos: BlockPos, assetManager: AssetManager): TerrainBlock

}