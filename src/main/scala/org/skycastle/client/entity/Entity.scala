package org.skycastle.client.entity

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
case class Entity(id: Symbol,
                  appearance: Appearance,
                  location: Location) {




}
