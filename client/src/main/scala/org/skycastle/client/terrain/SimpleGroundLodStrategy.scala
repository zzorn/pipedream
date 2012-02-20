package org.skycastle.client.terrain

import javax.vecmath.Vector3d
import org.skycastle.utils.MathUtils
import java.util.HashSet

/**
 *
 */
class SimpleGroundLodStrategy(blocksToTransition: Double = 2, hysteresis: Double = 0.5) extends GroundLodStrategy {
  require(hysteresis >= 0, "Hysteresis should be in range 0.., but it was " + hysteresis)

  def checkBlock(cameraPos: Vector3d, blockPos: BlockPos, terrainFunction: TerrainFunction, sizeSettings: GroundSizeSettings): LodCheckResult = {

    val blockSize = sizeSettings.calculateBlockSize(blockPos)
    val blockCenter = sizeSettings.calculateCenterPos(blockPos, terrainFunction, blockSize)
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