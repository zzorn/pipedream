package org.skycastle.client.terrain

/**
 * Creates terrain blocks for given locations at given level of detail.
 */
trait TerrainBlockSource {

  def createBlock(pos: BlockPos): TerrainBlock

}