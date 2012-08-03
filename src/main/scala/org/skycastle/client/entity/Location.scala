package org.skycastle.client.entity

import com.jme3.math.Vector3f
import org.skycastle.utils.Updateable
import org.skycastle.client.UpdatingField

/**
 * Position and velocity of something in a region.
 */
case class Location(@UpdatingField var regionId: Symbol = null,
                    @UpdatingField pos: Vector3f = new Vector3f(),
                    @UpdatingField vel: Vector3f = new Vector3f()) extends Updateable {

}
