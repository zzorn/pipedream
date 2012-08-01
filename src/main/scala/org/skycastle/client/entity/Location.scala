package org.skycastle.client.entity

import com.jme3.math.Vector3f

/**
 * Position and velocity of something in a region.
 */
case class Location(regionId: Symbol, pos: Vector3f = new Vector3f(), vel: Vector3f = new Vector3f()) {

}
