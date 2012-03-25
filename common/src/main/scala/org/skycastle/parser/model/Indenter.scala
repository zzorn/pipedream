package org.skycastle.parser.model

/**
 * Utility class for indenting
 */
case class Indenter(indent: Int) {

  val indentStr = "  ";

  def increase(): Indenter = Indenter(indent + 1)
  def decrease(): Indenter = Indenter(indent - 1)

  def newlineIndent = "\n" + indentStr * indent

  override def toString = indentStr * indent
}