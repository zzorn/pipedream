package org.skycastle.client.terrain.tasks

import java.util.Collections
import org.skycastle.client.terrain.{TerrainBlockSource, TerrainBlock, BlockPos}

/**
 *
 */
case class BlockMerge(block: BlockPos, source: TerrainBlockSource) extends GroundUpdateTask {

  protected def doTask() {
    // Create and populate the merged block
    blocksToAdd = Collections.singletonList(source.createBlock(block))

    // Remove old children
    blocksToRemove = block.children
  }

}