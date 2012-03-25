package org.skycastle.parser.syntaxtree

import java.util.ArrayList
import org.skycastle.parser.model.SyntaxError

/**
 *
 */
trait Reference extends AstNode {

  private var refNode: Option[AstNode] = None
  
  def referencedNode: Option[AstNode] = {
    if (!refNode.isDefined) refNode = getNodeAtPath(path)
    refNode
  }
  
  def path: List[Symbol]

  def checkForErrors(errors: ArrayList[SyntaxError]) {
    if (referencedNode.isEmpty) {
      addError(errors, "Reference '"+path.map(_.name).mkString(".") +"' not found")
    }
  }
}