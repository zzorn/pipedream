package org.skycastle.parser.model.defs

import org.skycastle.parser.model.{PathRef, SyntaxNode, TypeDef, Outputable}


/**
 * Some kind of definitions
 */
trait Def extends SyntaxNode {

  def name: Symbol

  def typeDef: TypeDef

  def getMember(name: Symbol): Option[Def]

  def getMemberByPath(path: PathRef): Option[Def] = getMemberByPath(path.path)

  def getMemberByPath(name: List[Symbol]): Option[Def] = {
    name match {
      case Nil => None
      case head :: Nil => getMember(head)
      case head :: tail => getMember(head).flatMap( m => m.getMemberByPath(tail) )
    }
  }
}
