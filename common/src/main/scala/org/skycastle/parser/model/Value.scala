package org.skycastle.parser.model

import org.skycastle.parser.Context

/**
 * A calculated value.
 */
@Deprecated
trait Value extends Context {

  def unwrappedValue: Any

}

@Deprecated
case class SimpleValue(unwrappedValue: Any, typeDef: TypeDef) extends Value