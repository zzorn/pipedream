package org.skycastle.world.crafting

import org.skycastle.world.artifact.Item
import org.scalaprops.Bean._
import org.skycastle.world.space.Space
import java.util.HashMap
import org.skycastle.world.{EntityId, WorkReq, Entity, EntityReq}
import java.lang.IllegalStateException
import org.skycastle.util.MultiSet

/**
 * Something under construction or repair.  Most artifacts are projects.
 * TODO: Should it be possible to change the design while something is under construction or completed?
 * Probably yes, same as renovation.
 */
// TODO: Space with slots for required parts
// TODO: Composite projects?
// TODO: Need to enumerate all part and work requirements up front, or it will be hard to e.g. estimate cost for a project.
//    But only some of the work (and parts?) might be possible to provide at a certain time, if they depend on each other
//    Some work may involve e.g. digging up earth, but that becomes a task of moving the soil to some location or input.
// TODO: How to express moving work, or e.g. lumbering or mining or packet transport, or soldiering, etc..
trait Project {

  private var _status: ProjectStatus = Planned
  private var _neededParts: MultiSet[EntityReq] = new MultiSet[EntityReq]()
  private var _neededWork: MultiSet[Work] = new MultiSet[Work]()
  private var _progress = 0.0

  def neededWork: Map[Work, Int] = _neededWork.map
  def neededParts: Map[EntityReq, Int] = _neededParts.map

  def status: ProjectStatus = _status

  def provideWork(work: Work, amount: Int = 1): Boolean = {
    if (_neededWork(work) > 0) {
      _neededWork.decrease(work, amount)
      updateProgress()
      true
    }
    else false
  }

  def providePart(part: Entity, amount: Int  = 1): Boolean = {
    neededParts.keySet.find( r => r.matches(part) ) match {
      case Some(r) =>
        if (_neededParts(r) > 0) {
          _neededParts.decrease(r, amount)
          part.delete()
          updateProgress()
          true
        }
        false
      case None =>
        false
    }
  }

  /**
   * Gives rough indication of construction progress, 0 to 1.
   */
  def constructionProgress: Double = _progress


  private def updateProgress() {

    if (_neededParts.isEmpty && _neededWork.isEmpty) {
      _status = Completed
      _progress = 1
      onCompleted()
    }

    // TODO

  }

  protected def onCompleted() {}

}
