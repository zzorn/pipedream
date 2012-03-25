package org.skycastle.parser.syntaxtree

import java.util.ArrayList
import runtime.DynamicContext
import org.skycastle.parser.model.{SyntaxError, Outputable}

/**
 *
 */
trait AstNode extends Outputable {

  var parentNode: Option[AstNode] = None

  def childNodes: Iterator[AstNode]
  protected def singleIt(n: AstNode): Iterator[AstNode] = if (n == null) Nil.iterator else List(n).iterator

  def checkForErrors(errors: ArrayList[SyntaxError])

  protected def addError(errors: ArrayList[SyntaxError], message: String) {
    errors.add(new SyntaxError(message, this))
  }



  def getNamedChildNode(name: Symbol): Option[AstNode] = None

  final def getNodeAtPath(path: List[Symbol]): Option[AstNode] = {
    getNamedChildNode(path.head) match {
      case Some(node) =>
        if (path.tail == Nil) Some(node)
        else node.getNodeAtPath(path.tail)
      case None =>
        parentNode.flatMap(_.getNodeAtPath(path))
    }
  }

}