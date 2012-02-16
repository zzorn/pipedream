package org.skycastle.client.terrain

/**
 * A mesh at a specific level of detail, optionally with a hole in the middle for an inner level,
 * and optionally surrounded by an outer level.
 */
class LodLevel(vertexDistance: Float,
               sideVertexCount: Int,
               var innerLevel: LodLevel = null,
               var outerLevel: LodLevel = null) {

}