package org.skycastle.world.artifact

/**
 * 
 */

class CompositeItem extends Item {

  def part[T <: Item](item: T): T = {
    item
  }

}