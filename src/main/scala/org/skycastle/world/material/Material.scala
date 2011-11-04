package org.skycastle.world.material

import org.scalaprops.Bean
import org.scalaprops.Bean._

/**
 * A single element, or a mix or alloy of several elements.
 */
trait Material extends Bean {
  // TODO: Get elemental properties

  val density = p('density, 1.0)

  // Hardness, elasticity , heat capacity, texture, etc etc


}

