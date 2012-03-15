package org.skycastle.parser.model.defs

import org.skycastle.parser.model.{Referable, TypeDef, Outputable}


/**
 * Some kind of definitions
 */
trait Def extends Outputable {

  def name: Symbol

  def typeDef: TypeDef

  def getMember(name: Symbol): Option[Def]

  def getMemberByPath(name: List[Symbol]): Option[Def] = {
    name match {
      case Nil => None
      case head :: Nil => getMember(head)
      case head :: tail => getMember(head).flatMap( m => m.getMemberByPath(tail) )
    }
  }
}
