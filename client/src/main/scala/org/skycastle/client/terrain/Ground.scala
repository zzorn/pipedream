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
import java.util.{Collections, HashSet, ArrayList, HashMap}

/**
 *
 */

// TODO: Split decision should not be based on distance to center of a grid, but to the center of the possible closest fine grained block

// TODO: Grid for top level blocks
//         -- appear block when distance to closest edge less than DTop, disappear when distance to closest larger than DTop+hysteresis
// TODO: Quadtree for smaller
//         -- Split a block when distance to its closest edge less than BlockLod, merge when distance to closest child over BlockLod+hysteresis

class Ground(sizeSettings: GroundSizeSettings,
             var terrainFunction: Terrain,
             source: TerrainBlockSource,
             camera: Camera = null,
             groundLodStrategy: GroundLodStrategy = new SimpleGroundLodStrategy()
              ) extends Node { 

  private val rootGrid = new HashMap[BlockPos, GroundTree]()
  private val rootBlocksToRemove = new HashSet[BlockPos]()
  private val cameraPos = new Vector3d(0,0,0)
  private val lastUpdatePos = new Vector3d(0,0,0)

  var minUpdateDistance: Double = sizeSettings.finestCellSize

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
    // Check if old root blocks should be removed
    checkRootBlocksToBeRemoved()

    // Check if new root blocks should be added
    checkRootBlocksToAdd
    
    // Update internal structure of current root blocks
    updateRootBlocks
  }

  private def checkRootBlocksToBeRemoved() {
    rootBlocksToRemove.clear()
    rootGrid.keySet() foreach {
      rootPos =>
        groundLodStrategy.checkBlock(cameraPos, rootPos, terrainFunction, sizeSettings) match {
          case _: RemoveBlock =>
            rootBlocksToRemove.add(rootPos)
          case _ => // No action
        }
    }
    rootBlocksToRemove foreach {
      rootPos =>
        rootGrid(rootPos).remove()
        rootGrid.remove(rootPos)
    }
    rootBlocksToRemove.clear()
  }

  private def checkRootBlocksToAdd {
    val newRootBlocks = groundLodStrategy.getRootBlocks(cameraPos, rootGrid.keySet(), sizeSettings)
    newRootBlocks foreach {
      newBlockPos =>
        val groundTree: GroundTree = new GroundTree(newBlockPos, this, source)
        rootGrid.put(newBlockPos, groundTree)
        groundTree.createTerrain()
    }
  }

  private def updateRootBlocks {
    rootGrid.values() foreach {
      rootTree =>
        rootTree.update(cameraPos, groundLodStrategy, terrainFunction, sizeSettings)
    }
  }


}