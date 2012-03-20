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

  def output(s: StringBuilder, indent: Int) {
    s.append(functionRef)

    s.append("(")
    if (!arguments.isEmpty) {
      outputSeparatedList(arguments, s, indent + 1)
    }

    s.append(")")
  }

  override def subNodes = arguments.iterator ++ singleIt(valueType)

  def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef =
    if (functionDef != null ) functionDef.returnType(visitedNodes) else null


  def calculate(context: Context): Value = {
    context.getValue(functionRef.path) match {
      case Some(func: Closure) => func.invokeWithCallerContext(arguments, context)
      case None                => throw new RunError("Can not find reference " + functionRef + ".", this)
      case Some(x: Value)      => throw new RunError("Could not invoke the reference '" + functionRef + "' with value "+x+".", this)
    }
  }

}

