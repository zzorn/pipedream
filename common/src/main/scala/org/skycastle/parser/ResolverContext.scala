package org.skycastle.parser

import model.defs.Def
import model.SyntaxNode

/**
 *
 */
case class ResolverContext(contextPath: List[SyntaxNode]) {

  def getDef(path: List[Symbol]): Option[Def] = {
    var referencedDefinition: Option[Def] = None
    contextPath.find({c =>
      referencedDefinition = c.getContextPathDef(path)
      referencedDefinition.isDefined
    })
    referencedDefinition
  }

  def subContext(pathElement: SyntaxNode): ResolverContext = {
    ResolverContext(pathElement :: contextPath)
  }

}