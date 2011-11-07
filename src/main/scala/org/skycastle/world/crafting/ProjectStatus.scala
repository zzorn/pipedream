package org.skycastle.world.crafting

/**
 * 
 */

abstract class ProjectStatus(val isFunctional: Boolean) {
}

object Planned extends ProjectStatus(false)
object UnderConstruction extends ProjectStatus(false)
object Completed extends ProjectStatus(true)
object Destroyed extends ProjectStatus(false)

