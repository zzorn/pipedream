package org.skycastle.client.terrain

import javax.vecmath.Vector3d

/**
 * Check wether to split or merge or do nothing for a block at a specific level of detail and position, given the camera position.
 */
trait GroundLodStrategy {

  def checkBlock(cameraPos: Vector3d, blockCenter: Vector3d, pos: BlockPos, worldSize: Double): LodCheckResult

}


sealed trait LodCheckResult

/**
 * Indicates that if the block doesn't exist, it should be created
 */
sealed trait AppearBlock extends LodCheckResult

/**
 * Indicates that if the block exists, it should be merged if possible.
 */
case object MergeBlock extends LodCheckResult

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
