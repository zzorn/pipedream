package org.skycastle.parser.model

import defs.Def

/**
 *
 */
trait SyntaxNode extends Outputable {

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


  def visitClasses[T <: SyntaxNode](kind: Class[T])(handler: (List[SyntaxNode], T) => Unit) {
    visit(x => x != null && kind.isAssignableFrom(x.getClass))(handler.asInstanceOf[(List[SyntaxNode], SyntaxNode) => Unit])
  }
  
  def visit(matcher: SyntaxNode => Boolean)(handler: (List[SyntaxNode], SyntaxNode) => Unit) {
    visitWithPath(matcher, Nil)(handler)
  }
  
  def visitWithPath(matcher: SyntaxNode => Boolean, context: List[SyntaxNode])(handler: (List[SyntaxNode], SyntaxNode) => Unit) {

    if (matcher(this)) handler(context, this)
    
    val subContext = if (hasContext) this :: context else context
    
    subNodes foreach {subNode => if (subNode != null) subNode.visitWithPath(matcher, subContext)(handler)}
  }

  protected def singleIt(n: SyntaxNode): Iterator[SyntaxNode] = if (n == null) Nil.iterator else List(n).iterator
  
}

object SyntaxNode {
  def getReferencedDefinition(context: List[SyntaxNode], path: List[Symbol]): Option[Def] = {
    var referencedDefinition: Option[Def] = None
    context.find({c =>
      referencedDefinition = c.getContextPathDef(path)
      referencedDefinition.isDefined
    })
    referencedDefinition
  }
}