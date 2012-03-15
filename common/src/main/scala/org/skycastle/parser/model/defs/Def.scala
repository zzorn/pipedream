package org.skycastle.parser.model.defs

import org.skycastle.parser.model.{Referable, TypeDef, Outputable}


/**
 * Some kind of definitions
 */
trait Def extends Outputable with Referable {

  def name: Symbol

  def typeDef: TypeDef

  def getMember(name: Symbol) = None
}
