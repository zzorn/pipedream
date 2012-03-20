package org.skycastle.parser.model

import org.skycastle.parser.Context

/**
 * A calculated value.
 */
trait Value extends Context {

  def unwrappedValue: Any

}

case class SimpleValue(unwrappedValue: Any, typeDef: TypeDef) extends Value