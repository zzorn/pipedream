package org.skycastle.scripting

/**
 *
 */

trait ScriptingService {

  def createInstance[T](path: String): T

  // TODO: Add createScript method with expected script object type.

}
