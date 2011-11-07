package org.skycastle.world

/**
 * 
 */
trait EntityReq {

  def matches(entity: Entity): Boolean

}