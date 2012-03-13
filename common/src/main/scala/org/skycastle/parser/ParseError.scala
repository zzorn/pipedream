package org.skycastle.parser

import scala.util.parsing.input.Position

/**
 * Describes an error in parsing
 */
class ParseError(message: String, pos: Position, name: String) extends Throwable(toString()) {
  override def toString = "Error when parsing " + (if (name != null) name else "") + " at " + pos + ": " + message + "\n" + pos.longString
}