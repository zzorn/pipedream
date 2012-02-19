package org.skycastle.client.terrain

import com.jme3.scene.Node
import scala.collection.JavaConversions._

/**
 * A grid in a simple quad tree,
 * used by Ground.
 */
// TODO: Use parent node, or make QuadGrid into a node?  A Spatial is a bit heavy, try without first
class QuadGrid(val pos: BlockPos, parentNode: Node, source: TerrainBlockSource) {

  private var block: TerrainBlock = null
  private var children: Array[QuadGrid] = null

  def isLeaf = children == null

  def createTerrain() {
    block = source.createBlock(pos)
    parentNode.attachChild(block)
  }

  def split() {
    if (isLeaf) {
      // Create children
      children = new Array[QuadGrid](4)
      var i = 0
      pos.children foreach { childPos =>
        val child = new QuadGrid(childPos, parentNode, source)
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
      children foreach { _.remove()}
      children = null
    }
  }
}

