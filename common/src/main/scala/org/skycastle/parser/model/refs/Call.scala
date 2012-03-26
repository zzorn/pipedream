package org.skycastle.parser.model.refs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model._
import defs.{Parameter, Def}
import org.skycastle.parser.{Context, RunError, ResolverContext}

/**
 *
 */
case class Call(functionRef: PathRef, arguments: List[Arg]) extends Expr {

  var functionDef: ReturnTyped = null
  var orderedArguments: List[Arg] = Nil

  // TODO: Order arguments in parameter order, fill in non-provided defaults with null

  override def subNodes = arguments.iterator

  def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef =
    if (functionDef != null ) functionDef.returnType(visitedNodes) else null


  def generateJavaCode(s: StringBuilder, indent: Indenter) {
    if (functionDef.)

    s.append(functionRef)

    s.append("(")
    outputSeparatedList(orderedArguments, s, indent.increase())
    s.append(")")
  }
}

