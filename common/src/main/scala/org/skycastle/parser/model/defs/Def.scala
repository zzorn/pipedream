package org.skycastle.parser.model.defs

import org.skycastle.parser.model.{TypeDef, Outputable}


/**
 * Some kind of definitions
 */
trait Def extends Outputable {

  def name: Symbol

  def typeDef: TypeDef

}
