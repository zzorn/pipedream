package org.skycastle.parser.model

import defs.Def
import org.skycastle.parser.{Context, ResolverContext}

/**
 *
 */
trait SyntaxNode extends Outputable with Context {

  var parentNode: SyntaxNode = null

  def parentContext = parentNode

  def subNodes: Iterator[SyntaxNode] = Nil.iterator

  def hasContext = false
  def getContextNamedDef(name: Symbol): Option[Def] = None
  def getContextPathDef(path: List[Symbol]): Option[Def] = {
    path match {
      case Nil => None
      case List(name) => getContextNamedDef(name)
      case head :: tail => getContextNamedDef(head).flatMap(d => d.getContextPathDef(tail))
    }
  }


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

  protected def singleIt(n: SyntaxNode): Iterator[SyntaxNode] = if (n == null) Nil.iterator else List(n).iterator
  
}

