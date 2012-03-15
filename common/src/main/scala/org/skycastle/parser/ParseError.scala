package org.skycastle.parser

import scala.util.parsing.input.Position

/**
 * Describes an error in parsing
 */
class ParseError(message: String, pos: Position, name: String) extends Throwable(toString()) {
  override def toString = {
    try {
      "Error when parsing " + (if (name != null) name + " at " + pos  else "") + ": " + message + "\n" + (if (name != null) pos.longString else "")
    }
    catch {
      case e: Throwable => "Error when parsing " + name + ": " + message
    }
  }
}