package org.skycastle.client.terrain

import javax.vecmath.Vector3d
import org.skycastle.utils.MathUtils

/**
 *
 */
class SimpleGroundLodStrategy(baseHalvingDistance: Double = 50, hysteresis: Double = 0.25) extends GroundLodStrategy {

  def checkBlock(cameraPos: Vector3d, blockCenter: Vector3d, pos: BlockPos, blockWorldSize: Double): LodCheckResult = {

    val idealDistance = baseHalvingDistance * math.pow(2, pos.lodLevel)
    val minDistance = idealDistance * (1.0 - hysteresis)
    val maxDistance = idealDistance * (1.0 + hysteresis)

    val distanceToBlock: Double = MathUtils.distance(cameraPos, blockCenter) - blockWorldSize * 0.5

    if (distanceToBlock < minDistance) SplitBlock
    else if (distanceToBlock > maxDistance) MergeBlock
    else if (distanceToBlock <= idealDistance) KeepOrAppearBlock
    else JustKeepBlock
  }

}