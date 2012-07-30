package org.skycastle.utils

/**
 * Something that handles a sub-area of responsibility.
 */
trait Service {

  /**
   * Called when the application starts up.
   */
  def startup() {}

  /**
   * Called when the application closes down.
   */
  def shutdown() {}

}