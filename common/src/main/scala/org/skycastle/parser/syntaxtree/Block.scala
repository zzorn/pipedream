package org.skycastle.parser.syntaxtree

import java.util.ArrayList

/**
 *
 */
case class Block(constructorParameters: Option[List[Parameter]], namedExpressions: Map[Symbol, Expr]) extends AstNode {

  def output(s: StringBuilder, indent: Int) {

  }


  def checkForErrors(errors: ArrayList[SyntaxError]) {

  }


  override def getNamedChildNode(name: Symbol) = namedExpressions.get(name)
}