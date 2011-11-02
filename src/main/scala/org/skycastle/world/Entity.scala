package org.skycastle.world

import org.scalaprops.Bean
import java.lang.{IllegalArgumentException, IllegalStateException}

/**
 * 
 */
trait Entity extends Bean {

  private var _id: EntityId = null

  def id: EntityId = _id

  def id_=(newId: EntityId) {
    if (_id != null) throw new IllegalStateException("Entity " + _id + " already has an id, can not set it a new id.")
    if (newId == null) throw new IllegalArgumentException("newId should not be null")

    _id = newId
  }

  

}