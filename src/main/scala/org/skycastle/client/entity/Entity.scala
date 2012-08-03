package org.skycastle.client.entity

import org.skycastle.utils.Updateable
import org.skycastle.client.UpdatingField

/**
 * Some usually visible in-game object.
 *
 * Has appearance, location.
 * Affordances:
 * - possible to pick up / carry
 * - pushable, pullable, movable
 * - climbable
 * - list of possible work projects that can be started on the item
 *    - e.g. cut tree, dig up tree (for replanting), open door, unlock door, cut open door, etc.
 * - attackable
 * - Could inherit affordances from some common sets.
 * - whether it has any interface that can be controlled
 *
 */
case class Entity(var id: Symbol,
                  @UpdatingField var appearance: Appearance,
                  @UpdatingField var location: Location) extends Updateable {

  //private var listeners: Set[EntityListener] = Set()


  //def addListener(listener: EntityListener)




}
