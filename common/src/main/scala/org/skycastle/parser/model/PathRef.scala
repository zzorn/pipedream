package org.skycastle.parser.model

/**
 * Reference to a specific definition or module or package, relative to the project root or the current context.
 */
case class PathRef(path: List[Symbol]) {
  require(!path.isEmpty, "Path should not be empty")

  override def toString = path.map(p => p.name).mkString(".")

  def lastName: Symbol = path.last
  
}
