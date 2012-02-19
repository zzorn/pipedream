package org.skycastle.client.terrain.tasks

import org.skycastle.client.terrain.{TerrainBlock, BlockPos}
import java.util.{Collections, HashSet}

/**
 *
 */
trait GroundUpdateTask {

  /**
   * The affected block.
   * (The parent block for merge or split tasks)
   */
  def block: BlockPos

  private var started_ = false
  private var ready_ = false
  private var canceled_ = false

  final def started: Boolean = started_
  final def ready: Boolean = ready_
  final def canceled: Boolean = canceled_

  final def start() {
    if (!started) {
      started_ = true

      // TODO: Run in worker thread / task manager
      doTask()
      ready_ = true
    }
  }

  var blocksToRemove: java.util.Set[BlockPos] = Collections.emptySet[BlockPos]()
  var blocksToAdd: java.util.List[TerrainBlock] = Collections.emptyList[TerrainBlock]()

  protected def doTask()

  def cancel() {
    // TODO: Cancel any running computation and discard it
    canceled_ = true
  }

}