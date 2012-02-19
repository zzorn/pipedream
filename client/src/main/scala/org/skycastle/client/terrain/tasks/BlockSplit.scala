package org.skycastle.client.terrain.tasks

import java.util.{ArrayList, Collections}
import org.skycastle.client.terrain.{TerrainBlock, TerrainBlockSource, BlockPos}

import scala.collection.JavaConversions._

/**
 *
 */
case class BlockSplit(block: BlockPos, source: TerrainBlockSource) extends GroundUpdateTask {

  protected def doTask() {

    // Create child blocks
    val blocks = new ArrayList[TerrainBlock](4)
    block.children foreach { childPos =>
      blocks.add(source.createBlock(childPos))
    }
    blocksToAdd = blocks

    // Remove parent block
    blocksToRemove = Collections.singleton(block)
  }

}