package org.skycastle.client.terrain.view

import javax.vecmath.Vector3d
import org.skycastle.utils.MathUtils
import java.util.HashSet
import org.skycastle.client.terrain.{BlockPosCache, BlockPos}
import org.skycastle.client.terrain.definition.{GroundSizeSettings, GroundDef}

/**
 *
 */
class SimpleGroundLodStrategy(blocksToTransition: Double = 2, hysteresis: Double = 0.5) extends GroundLodStrategy {
  require(hysteresis >= 0, "Hysteresis should be in range 0.., but it was " + hysteresis)

  def checkBlock(cameraPos: Vector3d, blockPos: BlockPos, terrainFunction: GroundDef, sizeSettings: GroundSizeSettings): LodCheckResult = {

    val blockSize = sizeSettings.calculateBlockSize(blockPos)
    val (x, z) = sizeSettings.calculateCenterAtZeroHeight(blockPos, blockSize)
    val y = terrainFunction.getHeight(x, z, blockSize / 4.0)
    val blockCenter = new Vector3d(x, y, z)
    val distance: Double = math.max(0, MathUtils.distance(cameraPos, blockCenter))

    val idealDistance = blocksToTransition * blockSize
    val splitDistance = idealDistance - blockSize * hysteresis
    val mergeDistance = idealDistance + blockSize * hysteresis

    if (distance < splitDistance) SplitBlock
    else if (distance > mergeDistance) MergeBlock
    else if (distance <= idealDistance) KeepOrAppearBlock
    else JustKeepBlock
  }


  def getRootBlocks(cameraPos: Vector3d, existingBlocks: java.util.Set[BlockPos], sizeSettings: GroundSizeSettings): java.util.Set[BlockPos] = {

    val newFoundBlocks = new HashSet[BlockPos]()

    val blockScanRadius = math.ceil(blocksToTransition).toInt

    val blockAtCamera = sizeSettings.blockPosAt(cameraPos, sizeSettings.maxLodLevel)

    var z = blockAtCamera.zPos - blockScanRadius
    var x = 0
    val endZ = blockAtCamera.zPos + blockScanRadius
    val endX = blockAtCamera.xPos + blockScanRadius
    while (z < endZ) {
      x = blockAtCamera.xPos - blockScanRadius
      while (x < endX) {
        val blockPos = BlockPosCache(sizeSettings.maxLodLevel, x, z)
        if (!existingBlocks.contains(blockPos)) newFoundBlocks.add(blockPos)

        x += 1
      }

      z += 1
    }

    newFoundBlocks
  }

}