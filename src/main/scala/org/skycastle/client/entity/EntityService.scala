package org.skycastle.client.entity

import org.skycastle.utils.Service
import org.skycastle.client.ActionMethod

/**
 *
 */
trait EntityService extends Service {

  // TODO: One type of entities are special portal entities, which may either allow perception (of different kinds)
  // to the other region, and/or transport there.  They have a portal polygon, a transformation, a location, and a link to the region.
  // TODO: Make terrains specific kinds of entities as well, as well as building walls etc?
  // Terrains and walls just have more complicated collision shapes than simple item entities (some of which do not collide at all)
  // (TODO: Have some kind of slowdown for crowds, so you can't just run through, but will not have very annoying blocks either.
  // The higher the density / proximity, the higher the slowdown, up to some max (so after finding the 5 closest persons no point in finding more,
  // as it would not change the slowdown - or just keep track of nr of people / softly blocking things in a tile) - also slower to walk towards someone than away from them.
  // Same approach can be used for thick under-vegetation, branches, etc)

  @ActionMethod
  def addEntity(entityId: Symbol, data: Map[Symbol, Any])

  @ActionMethod
  def updateEntity(entityId: Symbol, change: Map[Symbol, Any])

  @ActionMethod
  def removeEntity(entityId: Symbol)

  /**
   * Specifies which entity is the players avatar in the game world.
   * The avatar entity can already exist, or can be added later.
   */
  @ActionMethod
  def setAvatarEntity(entityId: Symbol)

  /**
   * @return the entity that represents the player in the game world, or null if none yet specified.
   */
  def avatarEntity: Entity

  /**
   * Add listener that is notified when the avatar changes to another entity or null.
   */
  def addAvatarChangeListener(listener: (Entity) => Unit)

  /**
   * Remove avatar entity listener.
   */
  def removeAvatarChangeListener(listener: (Entity) => Unit)
}
