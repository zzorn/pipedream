package org.skycastle.client.terrain.definition

import javax.vecmath.Vector3d
import org.skycastle.client.terrain.{BlockPosCache, BlockPos}

/**
 * Settings for terrain size and resolution.
 */
case class GroundSizeSettings(blockCellCount: Int = 32,
                              finestCellSize: Double = 1.0,
                              maxLodLevel: Int = 10) {

  lazy val maxLodBlockSize = calculateBlockSize(maxLodLevel)

  def blockPosAt(pos: Vector3d, lodLevel: Int): BlockPos = {
    val size = calculateBlockSize(lodLevel)
    val x = math.floor(pos.x / size)
    val z = math.floor(pos.z / size)
    BlockPosCache(lodLevel, x.toInt, z.toInt)
  }

  def calculateBlockSize(blockPos: BlockPos): Double = {
    calculateBlockSize(blockPos.lodLevel)
  }

  def calculateBlockSize(lodLevel: Int): Double = {
    blockCellCount * finestCellSize * math.pow(2, lodLevel)
  }

  def calculateCellSize(lodLevel: Int): Double = {
    finestCellSize * math.pow(2, lodLevel)
  }

  def powScale(lodLevel: Int): Double = {
    math.pow(2, lodLevel)
  }

  /*
    def calculateCenterPos(blockPos: BlockPos, terrainFunction: Terrain): Vector3d = {
      calculateCenterPos(blockPos, terrainFunction, calculateBlockSize(blockPos))
    }

    def calculateCenterPos(blockPos: BlockPos, terrainFunction: Terrain, blockSize: Double): Vector3d = {
      val centerX = blockSize * (blockPos.xPos + 0.5)
      val centerZ = blockSize * (blockPos.zPos + 0.5)
      val centerY = terrainFunction.getHeight(centerX, centerZ)
      new Vector3d(centerX, centerY, centerZ)
    }
  */
  def calculateTopLeft(blockPos: BlockPos, blockSize: Double): (Double, Double) = {
    val x = blockSize * blockPos.xPos
    val z = blockSize * blockPos.zPos
    (x, z)
  }

  def calculateCenterAtZeroHeight(blockPos: BlockPos, blockSize: Double): (Double, Double) = {
    val x = blockSize * (blockPos.xPos + 0.5)
    val z = blockSize * (blockPos.zPos + 0.5)
    (x, z)
  }

}