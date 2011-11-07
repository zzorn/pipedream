package org.skycastle.world.crafting

import org.skycastle.world.artifact.Item
import org.scalaprops.Bean._
import org.skycastle.world.space.Space
import java.util.HashMap
import org.skycastle.world.{EntityId, WorkReq, Entity, EntityReq}
import java.lang.IllegalStateException

/**
 * Something under construction or repair.  Most artifacts are projects.
 * TODO: Should it be possible to change the design while something is under construction or completed?
 * Probably yes, same as renovation.
 */
// TODO: Space with slots for required parts
// TODO: Composite projects?
trait Project {

  private val parts = new HashMap[EntityId, Entity]()

  private var _status: ProjectStatus = Planned
  private var _neededParts: List[EntityReq] = Nil
  private var _neededWork: List[WorkReq] = Nil
  private var _progress = 0.0

  def neededWork: List[WorkReq] = _neededWork

  def neededParts: List[EntityReq] = _neededParts

  def status: ProjectStatus = _status

  def addWork(work: Work): Boolean = {
    _neededWork.find( r => r.matches(work) ) match {
      case Some(r) =>
        _neededWork = _neededWork.filterNot( _ == r)
        updateProgress()
        true
      case None =>
        false
    }
  }

  def addPart(part: Entity): Boolean = {
    neededParts.find( r => r.matches(part) ) match {
      case Some(r) =>
        part.delete()
        _neededParts = _neededParts.filterNot( _ == r)
        updateProgress()
        true
      case None =>
        false
    }
  }

  /**
   * Gives rough indication of construction progress, 0 to 1.
   */
  def constructionProgress: Double = _progress


  private def updateProgress() {

    _neededParts :::= updateNeededParts()
    _neededWork :::= updateNeededWork()

    if (_neededParts.isEmpty && _neededWork.isEmpty) {
      _status = Completed
      onCompleted()
    }

    // TODO

  }

  protected def updateNeededWork(): List[WorkReq]
  protected def updateNeededParts(): List[EntityReq]

  protected def onCompleted() {}

}
