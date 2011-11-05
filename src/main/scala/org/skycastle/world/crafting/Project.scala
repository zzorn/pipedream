package org.skycastle.world.crafting

import org.skycastle.world.artifact.Item
import org.scalaprops.Bean._

/**
 * Something under construction or repair.  Most artifacts are projects.
 * TODO: Should it be possible to change the design while something is under construction or completed?
 * Probably yes, same as renovation.
 */
trait Project {

  def neededWork: List[Work]

  def neededParts: List[Item]

  def status: ProjectStatus

  /**
   * Gives rough indication of construction progress, 0 to 1.
   */
  def constructionProgress: Double



}