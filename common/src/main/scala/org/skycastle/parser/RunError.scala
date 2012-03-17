package org.skycastle.parser

import model.SyntaxNode

/**
 *
 */
class RunError(msg: String, location: SyntaxNode) extends Exception {
  override def getMessage = msg + " at " + location
}