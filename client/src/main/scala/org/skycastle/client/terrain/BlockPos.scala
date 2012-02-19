package org.skycastle.client.terrain

import javax.vecmath.Vector3d
import java.util.{HashMap, HashSet}

/**
 * Holds the LOD level and position of a block.
 */
case class BlockPos(lodLevel: Int, xPos: Int, zPos: Int) {


  /**
   * Blocks on the same level as this block, including diagonal blocks
   */
  lazy val neighbors: HashSet[BlockPos] = {
    val set: HashSet[BlockPos] = new HashSet[BlockPos]()
    set.add(BlockPosCache(lodLevel, xPos - 1, zPos - 1))
    set.add(BlockPosCache(lodLevel, xPos    , zPos - 1))
    set.add(BlockPosCache(lodLevel, xPos + 1, zPos - 1))

    set.add(BlockPosCache(lodLevel, xPos - 1, zPos))

    set.add(BlockPosCache(lodLevel, xPos + 1, zPos))

    set.add(BlockPosCache(lodLevel, xPos - 1, zPos + 1))
    set.add(BlockPosCache(lodLevel, xPos    , zPos + 1))
    set.add(BlockPosCache(lodLevel, xPos + 1, zPos + 1))
    set
  }

  /**
   * Blocks with the same parent as this block, excluding this block
   */
  lazy val siblings: HashSet[BlockPos] = {
    val set: HashSet[BlockPos] = new HashSet(parent.children)
    set.remove(this)
    set
  }

  /**
   * Blocks on a more detailed level as this block, in the area this block covers
   */
  lazy val children: HashSet[BlockPos] = {
    val set: HashSet[BlockPos] = new HashSet[BlockPos]()
    set.add(BlockPosCache(lodLevel - 1, xPos * 2    , zPos * 2    ))
    set.add(BlockPosCache(lodLevel - 1, xPos * 2 + 1, zPos * 2    ))
    set.add(BlockPosCache(lodLevel - 1, xPos * 2    , zPos * 2 + 1))
    set.add(BlockPosCache(lodLevel - 1, xPos * 2 + 1, zPos * 2 + 1))
    set
  }

  /**
   * The block on a more coarse level than this block, that contains the area this block covers
   */
  lazy val parent: BlockPos = {
    BlockPosCache(lodLevel + 1,
      if (xPos < 0) (xPos - 1) / 2  else xPos / 2,
      if (zPos < 0) (zPos - 1) / 2  else zPos / 2)
  }

  def ancestor(level: Int): BlockPos = {
    if (level >= lodLevel) this
    else parent.ancestor(level)
  }

}

object BlockPosCache {

  // TODO: memory freeing cache
  private var cache = new HashMap[(Int, Int, Int), BlockPos](200)

  def apply(lodLevel: Int, xPos: Int, zPos: Int): BlockPos = {
    val key: (Int, Int, Int) = (lodLevel, xPos, zPos)
    var pos: BlockPos = cache.get(key)
    if (pos == null) {
      pos = new BlockPos(lodLevel, xPos, zPos)
      cache.put(key, pos)
    }
    pos
  }
  
}