package org.skycastle.functions

import org.skycastle.utils.RandomUtils

/**
 *
 */

trait SeededFunc {

  private var _seed: Long = RandomUtils.randomSeed

  def seed: Long = _seed
  def seed_=(s: Long) {
    _seed = s
    seedChanged()
  }

  def getSeed(): Long = seed
  def setSeed(s: Long) {seed = s}

  protected def seedChanged() {}

}
