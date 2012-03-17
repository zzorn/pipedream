package org.skycastle.parser.model.defs

import org.skycastle.parser.model._
import expressions.Expr
import collection.immutable.List
import refs.Arg
import org.skycastle.parser.RunError


/**
 *
 */
case class FunDef(name: Symbol,
                  parameters: List[Parameter],
                  declaredReturnType: Option[TypeDef],
                  expression: Expr) extends Def with ValueTyped with ReturnTyped with Callable {

  private val paramsByName: Map[Symbol, Parameter] = parameters.map(p => p.name -> p).toMap

  def output(s: StringBuilder, indent: Int) {
    createIndent(s, indent)
    s.append("fun ")
      .append(name.name)
      .append("(")

    outputSeparatedList(parameters, s, indent + 1)
    s.append(")")

    if (returnType != null) {
      s.append(": ")
      returnType.output(s, indent)
    }
    s.append(" = ")

    expression.output(s, indent + 1)
  }

  override def subNodes = parameters.iterator ++ declaredReturnType.iterator ++ singleIt(expression)

  def getMember(name: Symbol) = None

  override def hasContext = true
  override def getContextNamedDef(name: Symbol): Option[Def] = paramsByName.get(name)

  def nameAndSignature = name.name + "("+parameters.mkString(", ")+"): " + (if (returnType == null) "[UnknownType]" else returnType.toString)

  def invoke(args: List[Arg]): Expr = {

    val context = new Context()

    var i = 0
    args foreach {a =>
      if (a.paramName.isDefined) {
        if (!paramsByName.contains(a.paramName.get)) 
          throw new RunError("Function "+name.name+" has no parameter named " + a.paramName.get.name, this)
        else 
          context.addBinding(a.paramName.get, a.value)
      }
      else {
        if (i >= parameters.size)
          throw new RunError("Too many paramters for function "+name.name, this)
        else {
          context.addBinding(parameters(i).name, a.value)
          i += 1
        }
      }
    }

    parameters foreach {p =>
      if (!context.hasBinding(p.name)){
        if (p.defaultValue.isDefined) {
          context.addBinding(p.name, p.defaultValue.get.calculate(context))
        }
        else {
          throw new RunError("Required parameter '"+p.name.name+"' not provided for function "+name.name, this)
        }
      }
    }

    expression.calculate(context)
  }


  protected def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef = {
    if (declaredReturnType.isDefined) {
      FunType(parameters.map(p => p.valueType(visitedNodes)), declaredReturnType.get)
    }
    else {
      val retType = expression.valueType(visitedNodes)
      if (retType == null) null
      else FunType(parameters.map(p => p.valueType(visitedNodes)), retType)
    }
  }

}