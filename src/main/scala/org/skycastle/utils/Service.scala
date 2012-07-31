package org.skycastle.utils

/**
 * Something that handles a sub-area of responsibility.
 */
trait Service {

  /**
   * @return name of the service.  For logging etc.
   */
  def serviceName: String = getClass.getSimpleName

  /**
   * Called when the application starts up.
   */
  def startup() {}

  /**
   * Called when the application closes down.
   */
  def shutdown() {}

}