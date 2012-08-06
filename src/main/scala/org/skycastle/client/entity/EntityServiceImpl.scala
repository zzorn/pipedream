package org.skycastle.client.entity

import org.skycastle.utils.{Logging, ParameterChecker}
import java.util
import org.skycastle.client.ClientServices

// TODO: Update regions with the entities that are in it, also when entity region changes.
class EntityServiceImpl(services: ClientServices) extends EntityService with Logging {

  private val entities: util.HashMap[Symbol, Entity] = new util.HashMap[Symbol, Entity]()

  private var avatarEntityId: Symbol = null
  private var avatarListeners: Set[(Entity) => Unit] = Set()


  def addEntity(entityId: Symbol, data: Map[Symbol, Any]) {
    ParameterChecker.requireIsIdentifier(entityId, 'entityId)
    if (entities.containsKey(entityId)) throw new Error("An entity with the id '"+entityId.name+"' already exists")

    // Create entity
    val entity = new Entity(entityId, new PlaceholderAppearance(), new Location())

    // Apply data
    entity.update(data)

    // Add to region service to keep track of entities in different regions.
    services.regionService.addEntity(entity)

    // Check if this happens to be the avatar entity
    if (entityId == avatarEntityId) {
      notifyAvatarListeners(entity)
    }
  }

  def updateEntity(entityId: Symbol, change: Map[Symbol, Any]) {
    ParameterChecker.requireIsIdentifier(entityId, 'entityId)
    ParameterChecker.requireNotNull(change, 'change)

    // Get entity
    val entity: Entity = entities.get(entityId)
    if (entity == null) throw new Error("No entity with the id '"+entityId.name+"' found")

    // Update
    entity.update(change)
  }

  def removeEntity(entityId: Symbol) {
    ParameterChecker.requireIsIdentifier(entityId, 'entityId)

    if (entities.containsKey(entityId)) {
      val removedEntity = entities.get(entityId)

      // Notify entity about removal
      removedEntity.onRemoved()

      entities.remove(entityId)
    }
  }

  def setAvatarEntity(entityId: Symbol) {
    if (avatarEntityId != entityId) {
      avatarEntityId = entityId

      if (avatarEntityId == null) {
        // No avatar (avatar died, or creating new character, etc)
        notifyAvatarListeners(null)
      }
      else if (entities.containsKey(entityId)) {
        // Found avatar
        notifyAvatarListeners(entities.get(avatarEntityId))
      }
      else {
        // No such entity found, avatar coming later presumably
        notifyAvatarListeners(null)
        log.debug("Avatar entity with id '"+entityId.name+"' not found, waiting for avatar entity to appear later.")
      }
    }
  }

  def avatarEntity: Entity = {
    entities.get(avatarEntityId)
  }

  def addAvatarChangeListener(listener: (Entity) => Unit) {
    avatarListeners += listener
  }

  def removeAvatarChangeListener(listener: (Entity) => Unit) {
    avatarListeners -= listener
  }

  private def notifyAvatarListeners(avatar: Entity) {
    avatarListeners foreach {al => al(avatar)}
  }

}
