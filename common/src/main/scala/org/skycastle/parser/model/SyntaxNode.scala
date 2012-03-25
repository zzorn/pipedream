package org.skycastle.parser.model

import defs.Def
import org.skycastle.parser.{Context, ResolverContext}

/**
 *
 */
trait SyntaxNode extends Context {

  var parentNode: SyntaxNode = null

  override def parentContext = parentNode

  /**
   * @return iterator for all subnodes of this syntax node.
   */
  def subNodes: Iterator[SyntaxNode] = Nil.iterator

  /**
   * Helper for creating subnode iterator
   */
  protected def singleIt(n: SyntaxNode): Iterator[SyntaxNode] = if (n == null) Nil.iterator else List(n).iterator

  /**
   * @return subnode with the specified name, if there is one.
   */
  def getNamedNode(name: Symbol): Option[SyntaxNode] = None

  /**
   * @return node with the specified path, either from this node, or any parent node.
   */
  final def getVisibleNamedNode(path: List[Symbol]): Option[SyntaxNode] = {
    getNamedNode(path.head) match {
      case Some(node) =>
        if (path.tail == Nil) Some(node)
        else node.getVisibleNamedNode(path.tail)
      case None =>
        if (parentNode != null) parentNode.getVisibleNamedNode(path)
        else None
    }
  }


  /*
  def hasContext = false
  def getContextNamedDef(name: Symbol): Option[Def] = None
  def getContextPathDef(path: List[Symbol]): Option[Def] = {
    path match {
      case Nil => None
      case List(name) => getContextNamedDef(name)
      case head :: tail => getContextNamedDef(head).flatMap(d => d.getContextPathDef(tail))
    }
  }
  */

  final def initializeParentNodes() {
    subNodes foreach {subNode =>
      subNode.parentNode = this
      subNode.initializeParentNodes()
    }
  }
  
  /**
   * Check the node for any errors, store them in provided parameter.
   */
  def checkForErrors(errors: Errors) {}


  /**
   * Check this node and all subnodes for any errors, store them in provided parameter.
   */
  final def checkTreeForErrors(errors: Errors) {
    checkForErrors(errors)
    subNodes foreach {_.checkTreeForErrors(errors) }
  }
  
  /*
  def visitClasses[T <: SyntaxNode](kind: Class[T], depthFirst: Boolean = false)(handler: (ResolverContext, SyntaxNode, T) => Unit) {
    visit(x => x != null && kind.isAssignableFrom(x.getClass), depthFirst)(handler.asInstanceOf[(ResolverContext, SyntaxNode, SyntaxNode) => Unit])
  }
  
  def visit(matcher: SyntaxNode => Boolean, depthFirst: Boolean = false)(handler: (ResolverContext, SyntaxNode, SyntaxNode) => Unit) {
    visitWithPath(matcher, ResolverContext(Nil), depthFirst)(handler)
  }
  
  def visitWithPath(matcher: SyntaxNode => Boolean, context: ResolverContext, depthFirst: Boolean = false, parent: SyntaxNode = null)(handler: (ResolverContext, SyntaxNode, SyntaxNode) => Unit) {

    val subContext = if (hasContext) context.subContext(this) else context

    if (!depthFirst && matcher(this)) handler(subContext, parent, this)
    
    subNodes foreach {subNode => if (subNode != null) subNode.visitWithPath(matcher, subContext, parent = this)(handler)}

    if (depthFirst && matcher(this)) handler(subContext, parent, this)
  }
  */



  /**
   * Generate the java code for this construct.
   */
  def generateJavaCode(s: StringBuilder, indent: Indenter)

  protected def outputSeparatedList(list: Iterable[SyntaxNode], s: StringBuilder, indent: Indenter, separator: String = ", ") {
    var first = true
    list foreach {l =>
      // Separator
      if (first) first = false
      else s.append(separator)

      // Entry
      l.generateJavaCode(s, indent)
    }
  }

  protected def outputTerminatedList(list: Iterable[SyntaxNode], s: StringBuilder, indent: Indenter, terminator: String = "; ") {
    list foreach {l =>
      // Entry
      l.generateJavaCode(s, indent)

      // Terminator
      s.append(terminator)
    }
  }

}

