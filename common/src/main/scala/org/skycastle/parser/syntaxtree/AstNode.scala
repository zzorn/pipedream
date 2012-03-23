package org.skycastle.parser.syntaxtree

import java.util.ArrayList
import org.skycastle.parser.model.Outputable

/**
 *
 */
trait AstNode extends Outputable {

  var parentNode: AstNode = null

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
        if (parentNode != null) parentNode.getNodeAtPath(path)
        else None
    }
  }

}