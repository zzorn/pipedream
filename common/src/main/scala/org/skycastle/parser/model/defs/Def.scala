package org.skycastle.parser.model.defs

import org.skycastle.parser.model._
import org.skycastle.parser.Context


/**
 * Some kind of definitions
 */
trait Def extends SyntaxNode {



  def name: Symbol


  def getMember(name: Symbol): Option[Def]
  def getMemberByPath(path: PathRef): Option[Def] = getMemberByPath(path.path)
  def getMemberByPath(name: List[Symbol]): Option[Def] = {
    name match {
      case Nil => None
      case head :: Nil => getMember(head)
      case head :: tail => getMember(head).flatMap( m => m.getMemberByPath(tail) )
    }
  }

  
  def calculate(context: Context): Value
}
