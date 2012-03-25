package org.skycastle.parser.syntaxtree

import java.util.ArrayList
import org.skycastle.parser.model.SyntaxError

/**
 *
 */
case class Block(constructorParameters: Option[List[Parameter]], namedExpressions: Map[Symbol, Expr]) extends AstNode with Parametrized {

  def output(s: StringBuilder, indent: Int) {

  }


  def checkForErrors(errors: ArrayList[SyntaxError]) {

  }


  override def getNamedChildNode(name: Symbol) = namedExpressions.get(name)

  def childNodes: Iterator[AstNode] = constructorParameters.getOrElse(Nil).iterator ++ namedExpressions.values

  def parameters = constructorParameters.getOrElse(Nil)


}