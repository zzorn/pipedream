package org.skycastle.client.terrain

import com.jme3.scene.Node
import definition.GroundDef
import scala.collection.JavaConversions._
import javax.vecmath.Vector3d
import com.jme3.asset.AssetManager

/**
 * A cell in a simple quad tree,
 * used by Ground.
 */
// TODO: Use parent node, or make QuadGrid into a node?  A Spatial is a bit heavy, try without first
// TODO: In future make GroundTree start calculation processes, and stop them if the tree is changed.
class GroundTree(val pos: BlockPos, parentNode: Node, source: TerrainBlockSource, assetManager: AssetManager) {


  private var block: TerrainBlock = null
  private var children: Array[GroundTree] = null

  def isLeaf = children == null


  def update(camPos: Vector3d, lodStrategy: GroundLodStrategy, terrainFunction: GroundDef, sizeSettings: GroundSizeSettings) {
    lodStrategy.checkBlock(camPos, pos, terrainFunction, sizeSettings) match {
      case SplitBlock =>
        split()
      case MergeBlock =>
        merge()
      case _ =>
        // No change to this block
    }

    // Update children
    if (children != null) {
      children foreach { c =>
        c.update(camPos, lodStrategy, terrainFunction, sizeSettings)
      }
    }
  }


  def createTerrain() {
    block = source.createBlock(pos, assetManager)
    parentNode.attachChild(block)
  }

  def split() {
    if (isLeaf && pos.lodLevel > 0) {
      // Create children
      children = new Array[GroundTree](4)
      var i = 0
      pos.children foreach {
        childPos =>
          val child = new GroundTree(childPos, parentNode, source, assetManager)
          child.createTerrain()
          children(i) = child
          i += 1
      }

      // Hide block if we have one
      if (block != null) {
        parentNode.detachChild(block)
      }
    }
  }

  def merge() {
    if (!isLeaf) {
      removeChildren()

      if (block != null) {
        // Show block if we have one
        if (!parentNode.hasChild(block)) parentNode.attachChild(block)
      }
      else {
        // Create it if we don't have it
        createTerrain()
      }
    }
  }

  def remove() {
    removeChildren
    removeBlock
  }

  private def removeBlock {
    if (block != null) {
      block.freeResources()
      parentNode.detachChild(block)
      block = null
    }
  }

  private def removeChildren() {
    if (children != null) {
      children foreach {
        _.remove()
      }
      children = null
    }
  }
}

