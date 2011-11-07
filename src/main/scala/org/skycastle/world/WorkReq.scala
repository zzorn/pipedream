package org.skycastle.world

import crafting.Work

/**
 * 
 */

trait WorkReq {
  def matches(work: Work): Boolean
}