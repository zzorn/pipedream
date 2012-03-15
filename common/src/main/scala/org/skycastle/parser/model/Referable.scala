package org.skycastle.parser.model

/**
 * Something that can be referred to with a PathRef (excluding local parameters).
 */
trait Referable {

  def name: Symbol

  def getMember(name: Symbol): Option[Referable]

  def getMemberByPath(name: List[Symbol]): Option[Referable] = {
    name match {
      case Nil => None
      case head :: Nil => getMember(head)
      case head :: tail => getMember(head).flatMap( m => m.getMemberByPath(tail) )
    }
  }

}
