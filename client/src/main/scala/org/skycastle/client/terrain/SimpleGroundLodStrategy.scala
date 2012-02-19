package org.skycastle.client.terrain

import javax.vecmath.Vector3d
import org.skycastle.utils.MathUtils

/**
 *
 */
class SimpleGroundLodStrategy(baseHalvingDistance: Double = 50, hysteresis: Double = 0.25) extends GroundLodStrategy {

  def checkBlock(cameraPos: Vector3d, blockCenter: Vector3d, pos: BlockPos): LodCheckResult = {

    val idealDistance = baseHalvingDistance * math.pow(2, pos.lodLevel)
    val minDistance = idealDistance * (1.0 - hysteresis)
    val maxDistance = idealDistance * (1.0 + hysteresis)

    val distanceToBlockSquared = MathUtils.distanceSquared(cameraPos, blockCenter)

    if (distanceToBlockSquared < minDistance*minDistance) SplitBlock
    else if (distanceToBlockSquared > maxDistance*maxDistance) MergeBlock
    else if (distanceToBlockSquared <= idealDistance ) KeepOrAppearBlock
    else JustKeepBlock
  }

}