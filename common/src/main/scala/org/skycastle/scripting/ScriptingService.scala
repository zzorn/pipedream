package org.skycastle.scripting

/**
 *
 */

trait ScriptingService {

  def createScript(path: String): Script

  // TODO: Add createScript method with expected script object type.

}
