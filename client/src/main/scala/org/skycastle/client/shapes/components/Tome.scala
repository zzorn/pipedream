package org.skycastle.client.shapes.components

import scala.reflect._

/**
 * Wraps a library entry, provides authorship etc info?
 */
class Tome() {

  @BeanProperty var name: String = null
  @BeanProperty var model: Model = null

}
