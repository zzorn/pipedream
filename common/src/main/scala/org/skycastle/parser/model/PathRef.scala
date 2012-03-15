package org.skycastle.parser.model

/**
 * Reference to a specific definition or module or package, relative to the project root or the current context.
 */
case class PathRef(path: List[Symbol]) {

  override def toString = path.map(p => p.name).mkString(".")

}
