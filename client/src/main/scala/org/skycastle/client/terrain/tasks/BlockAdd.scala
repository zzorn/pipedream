package org.skycastle.client.terrain.tasks

import org.skycastle.client.terrain.{TerrainBlock, TerrainBlockSource, BlockPos}
import java.util.Collections


/**
 *
 */
case class BlockAdd(block: BlockPos, source: TerrainBlockSource) extends GroundUpdateTask {

  protected def doTask() {
    // Create and populate the block
    blocksToAdd = Collections.singletonList(source.createBlock(block))
  }

}