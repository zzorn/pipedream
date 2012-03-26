package org.skycastle.scripting

/**
 *
 */
trait Script {

  def invoke(parameters: Map[Symbol, Any]): Any

}
