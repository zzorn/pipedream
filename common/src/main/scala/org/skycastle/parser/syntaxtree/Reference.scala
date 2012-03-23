package org.skycastle.parser.syntaxtree

import java.util.ArrayList

/**
 *
 */
trait Reference extends AstNode {

  def path: List[Symbol]

  def checkForErrors(errors: ArrayList[SyntaxError]) {
    if (getNodeAtPath(path).isEmpty) {
      addError(errors, "Reference '"+path.map(_.name).mkString(".") +"' not found")
    }
  }
}