package org.skycastle.client.terrain

import definition.GroundDef
import org.skycastle.utils.Service
import com.jme3.math.Matrix4f

/**
 * Service that allows updating terrain based on information from server.
 */
// TODO: These are updates to terrain entities. Are they needed at all, if we use regions for large scale terrain chunks?
trait TerrainService extends Service {

  /**
   * Indicates that a new terrain is visible (e.g. planet surface, large cave floor, projected holomap, etc).
   * @param id the id that will be used to refer to this terrain.
   * @param transform a transformation that should be applied to the whole terrain.
   * @param ground functions describing the terrain.
   *               In addition, will need a random seed from the visible terrain blocks, sent separately.
   */
  def addVisibleTerrain(id: Symbol, transform: Matrix4f, ground : GroundDef)

  /**
   * Called when a specific terrain is no longer perceivable to the player.
   */
  def removeVisibleTerrain(id: Symbol)

  /**
   * Called when a new high level terrain block appears in detection radius.
   * @param terrainId the terrain the block belongs to.
   * @param blockPos the position of the block on the terrain.
   * @param randomSeed local random seed for this terrain block.
   * @param ground the function describing the block (modifies any ground specified earlier for the terrain).
   */
  def addVisibleTerrainBlock(terrainId: Symbol, blockPos: BlockPos, randomSeed: Long, ground: GroundDef = null)

  /**
   * Called when a specific terrain block of a specific terrain is no longer visible to the player.
   */
  def removeVisibleTerrainBlock(terrainId: Symbol, blockPos: BlockPos)


}