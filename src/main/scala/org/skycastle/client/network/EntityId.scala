package org.skycastle.client.network

/**
 * A reference to an entity with the specified id.
 */
case class EntityId(id: Long)

case object UndefinedId extends EntityId(0)