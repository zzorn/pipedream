package org.skycastle.parser.model.module

import org.skycastle.parser.model.Value
import org.skycastle.parser.model.defs.Def
import org.skycastle.parser.Context

/**
 *
 */
case class ModuleValue(module: Module) extends Value {


  override def parentContext = module.parentContext

  override def getNestedValue(name: Symbol): Option[Value] = {
    module.getMember(name) map {d => d.calculate(module)}
  }

  def unwrappedValue = this
}