package org.skycastle.parser.model

import java.util.ArrayList

/**
 * Holds syntax errors
 */
class Errors {

  val errors: ArrayList[SyntaxError] = new ArrayList[SyntaxError]()
  
  def addError(error: SyntaxError) {
    errors.add(error)
  }
  
  def addError(message: String, node: SyntaxNode) {
    addError(new SyntaxError(message, node))
  }
  
}