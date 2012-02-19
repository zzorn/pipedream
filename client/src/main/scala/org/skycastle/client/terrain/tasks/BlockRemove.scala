package org.skycastle.client.terrain.tasks

import org.skycastle.client.terrain.BlockPos
import java.util.Collections

/**
 *
 */
case class BlockRemove(block: BlockPos) extends GroundUpdateTask {

  protected def doTask() {
    // Remove the block
    blocksToRemove = Collections.singleton(block)
  }

}