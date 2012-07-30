package org.skycastle.client.terrain.view

import org.skycastle.client.terrain.definition
import definition.GroundDef
import javax.vecmath.Vector3d
import java.util.{HashSet, HashMap}

/**
 * Check whether to split or merge or do nothing for a block at a specific level of detail and position, given the camera position.
 */
trait GroundLodStrategy {

  def checkBlock(cameraPos: Vector3d, blockPos: BlockPos, terrainFunction: GroundDef, sizeSettings: GroundSizeSettings): LodCheckResult

  /**
   * @return the root blocks that should be added.
   */
  def getRootBlocks(cameraPos: Vector3d, existingBlocks: java.util.Set[BlockPos], sizeSettings: GroundSizeSettings): java.util.Set[BlockPos]

}

sealed trait LodCheckResult

/**
 * Indicates that if the block doesn't exist, it should be created
 */
sealed trait AppearBlock extends LodCheckResult

/**
 * Indicates that if the block is at root level, it should be removed
 */
sealed trait RemoveBlock extends LodCheckResult

/**
 * Indicates that if the block exists, it should be merged if possible, or removed if it is at root level.
 */
case object MergeBlock extends LodCheckResult with RemoveBlock

/**
 * Indicates that if the block exists, it should be split.
 */
case object SplitBlock extends LodCheckResult with AppearBlock

/**
 * Indicates that if the block exists, it should be kept.
 */
sealed trait KeepBlock extends LodCheckResult

case object KeepOrAppearBlock extends KeepBlock with AppearBlock
case object JustKeepBlock extends KeepBlock
