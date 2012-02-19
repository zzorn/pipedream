package org.skycastle.client.terrain

import com.jme3.asset.AssetManager
import com.jme3.scene.Node
import com.jme3.terrain.geomipmap.TerrainLodControl
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator
import com.jme3.renderer.Camera
import com.jme3.math.Vector3f
import javax.vecmath.Vector3d
import scala.collection.JavaConversions._
import org.skycastle.utils.MathUtils
import com.sun.xml.internal.bind.v2.schemagen.MultiMap
import tasks._
import java.util.{Collections, HashSet, ArrayList, HashMap}

/**
 *
 */

// TODO: Split decision should not be based on distance to center of a grid, but to the center of the possible closest fine grained block

// TODO: Grid for top level blocks
//         -- appear block when distance to closest edge less than DTop, disappear when distance to closest larger than DTop+hysteresis
// TODO: Quadtree for smaller
//         -- Split a block when distance to its closest edge less than BlockLod, merge when distance to closest child over BlockLod+hysteresis

class Ground(blockCellCount: Int,
             smallestCellSize: Double,
             var terrainFunction: TerrainFunction,
             source: TerrainBlockSource,
             maxLod: Int = 10,
             camera: Camera = null,
             groundLodStrategy: GroundLodStrategy = new SimpleGroundLodStrategy()
              ) extends Node { 

  private val blocks = new HashMap[BlockPos, TerrainBlock](200)
  
  private val coveredMaxLodBlocks = new HashSet[BlockPos]()
  private val checkedMaxLodBlocks = new HashSet[BlockPos]()
  private val cameraPos = new Vector3d(0,0,0)
  private val lastUpdatePos = new Vector3d(0,0,0)
  private val blocksToSplit = new HashSet[BlockPos]()
  private val blocksToMerge = new HashSet[BlockPos]()
  private val ongoingTasks = new HashMap[BlockPos, GroundUpdateTask]()
  private val completedTasks = new HashSet[BlockPos]()

  private val maxLodBlockSize = blockCellCount * smallestCellSize * math.pow(2, maxLod)

  var minUpdateDistance: Double = 1

  if (camera != null) addControl(new GroundCameraControl(camera));

  def updateCameraPos(pos: Vector3f, timePerFrame: Float) {
    cameraPos.set(pos.x, pos.y, pos.z)

    // Check if we moved enough to do an update check
    if (MathUtils.distance(cameraPos, lastUpdatePos) > minUpdateDistance) {
      lastUpdatePos.set(cameraPos)

      updateBlocks()
    }

  }

  /**
   * Starts any needed updates for blocks, and checks for completed tasks.
   * Should be called regularly.
   */
  def updateBlocks() {
    // Check for new tasks
    if (checkBlockTasks()) {
      ongoingTasks.values() foreach { task =>
        if (!task.started) {
          task.start()
        }
      }
    }

    // Check for completed tasks
    completedTasks.clear()
    ongoingTasks.values() foreach { task =>
      if (task.ready) {
        completedTasks.add(task.block)
        handleTaskCompleted(task)
      }
    }
    completedTasks foreach { t => ongoingTasks.remove(t) }
  }

  private def handleTaskCompleted(task: GroundUpdateTask) {
    task.blocksToRemove foreach { blockPos =>
      val block = blocks.get(blockPos)
      if (block != null) {
        blocks.remove(blockPos)
        detachChild(block)
        block.freeResources()
      }
    }
    
    task.blocksToAdd foreach { block: TerrainBlock =>
      blocks.put(block.blockPos, block)
      attachChild(block)
    }
  }

  
  /**
   * Checks if blocks need to be added, removed, split, or merged.
   * @return true if any tasks were updated.
   */
  private def checkBlockTasks(): Boolean = {

    var tasksUpdated = false

    // Loop through the blocks, check if any needs to be split or merged
    // There's about a few hundred blocks, so not too much work
    blocksToMerge.clear()
    blocksToSplit.clear()
    coveredMaxLodBlocks.clear()
    blocks.entrySet() foreach {
      entry =>
        // Check if we should split or merge blocks
        groundLodStrategy.checkBlock(cameraPos, entry.getValue.blockCenter, entry.getKey, entry.getValue.worldSize) match {
          case MergeBlock => blocksToMerge.add(entry.getKey)
          case SplitBlock => blocksToSplit.add(entry.getKey)
          case _: KeepBlock => // Do nothing, keep block unchanged
        }
        
        // Also keep track of which of the highest level lod blocks we overlap
        coveredMaxLodBlocks.add(entry.getKey.ancestor(maxLod))
    }

    // Handle merges
    // Only merge if all blocks under a parent block want to merge
    blocksToMerge foreach {
      blockPos =>
        if (blocksToMerge.containsAll(blockPos.siblings)) {
          val parent: BlockPos = blockPos.parent

          if (parent.lodLevel <= maxLod) {
            // Initiate loading parent, when ready, remove the children, add the loaded block
            tasksUpdated ||= addTask(new BlockMerge(parent, source))
          }
          else {
            // Remove the blocks to merge, as they are top level blocks going out of view.
            parent.children foreach { oldTopLevelBlock =>
              tasksUpdated ||= addTask(new BlockRemove(oldTopLevelBlock))
            }
          }

        }
    }

    // Handle splits
    blocksToSplit foreach {
      blockPos =>
        if (blockPos.lodLevel > 0) {
          // Initiate loading of all children, when all ready, remove the parent, add the loaded blocks
          tasksUpdated ||= addTask(new BlockSplit(blockPos, source))
        }
    }

    // Go through all highest lod level blocks, check if their more distant neighbors that are not yet visible should become visible
    checkedMaxLodBlocks.clear()
    coveredMaxLodBlocks foreach {
      block =>
        block.neighbors foreach {
          neighbor =>
            if (!coveredMaxLodBlocks.contains(neighbor) && !checkedMaxLodBlocks.contains(neighbor)) {
              checkedMaxLodBlocks.add(neighbor)
              val center = neighbor.calculateCenterPos(maxLodBlockSize, terrainFunction)
              groundLodStrategy.checkBlock(cameraPos, center, neighbor, maxLodBlockSize) match {
                case _ : AppearBlock =>
                  // Create new top level block
                  tasksUpdated ||= addTask(new BlockAdd(neighbor, source))
                case _ => // The neighbor block is not in range to appear, do nothing
              }
            }
        }
    }

    // If there are no blocks, add one at current camera pos
    if (blocks.isEmpty) {
      val pos = BlockPosCache(maxLod,
        (cameraPos.x / maxLodBlockSize).toInt,
        (cameraPos.z / maxLodBlockSize).toInt)
      tasksUpdated ||= addTask(new BlockAdd(pos, source))
    }

    tasksUpdated
  }

  private def cancelAndRemoveTasksFor(blocks: HashSet[BlockPos]) {
    blocks foreach { blockPos =>
      cancelAndRemoveTaskFor(blockPos)
    }
  }

  private def cancelAndRemoveTaskFor(block: BlockPos) {
    val task = ongoingTasks.get(block)
    if (task != null) {
      task.cancel()
      ongoingTasks.remove(task)
    }
  }

  /**
   * @return true if the ongoing tasks were modified
   */
  private def addTask(task: GroundUpdateTask): Boolean = {
    // Cancel any splits of the children or merge of the parent
    cancelAndRemoveTasksFor(task.block.children)
    cancelAndRemoveTaskFor(task.block.parent)

    // Cancel conflicting tasks on same block
    val existingTask: GroundUpdateTask = ongoingTasks.get(task.block)
    if (existingTask != null) {
      if (existingTask.getClass != task.getClass) {
        cancelAndRemoveTaskFor(task.block)
        ongoingTasks.put(task.block, task)
        return true
      }
      // If there is already the correct task ongoing, we don't have to do anything.
      return false
    }
    else {
      // No existing ongoing task, start this
      ongoingTasks.put(task.block, task)
      return true
    }
  }


}