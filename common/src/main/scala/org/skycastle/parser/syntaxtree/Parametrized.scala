package org.skycastle.parser.syntaxtree

import org.skycastle.parser.model.refs.Arg
import org.skycastle.parser.model.{MutableContext, Value}
import org.skycastle.parser.{RunError, Context}
import runtime.{MutableDynamicContext, DynamicContext}


/**
 * Represents something that takes parameters.
 */
trait Parametrized {

  def parameters: List[Parameter]

  lazy val parametersByName: Map[Symbol, Parameter] = parameters.map(p => p.name -> p).toMap
  def parameterByName(name: Symbol): Option[Parameter] = parametersByName.get(name)
  def parameterByIndex(index: Int): Option[Parameter] = if (index < 0 || index >= parameters.size) None else Some(parameters(index))

  def invokeWithCallerContext(arguments: List[Argument], callLocation: AstNode, callerContext: DynamicContext, calleeContext: DynamicContext): Value = {

    val argumentValues = new MutableDynamicContext()

    // Bind argument names to argument values
    // Check that all arguments map to some parameter
    var index = 0
    arguments foreach {a: Argument =>
      if (a.paramName.isDefined) {
        if (parametersByName.contains(a.paramName.get))
          argumentValues.addBinding(a.paramName.get, a.valueExpr.calculate(callerContext))
        else
          throw new RunError("Invoked function has no parameter named '" + a.paramName.get.name + "'.", callLocation)
      }
      else {
        if (index < parameters.size) {
          argumentValues.addBinding(parameters(index).name, a.valueExpr.calculate(callerContext))
          index += 1
        }
        else {
          throw new RunError("Too many paramters for invoked function.", callLocation)
        }
      }
    }

    // Calculate default values
    // Check that all parameters had some argument or default argument provided
    parameters foreach {param =>
      if (!argumentValues.hasBinding(param.name)){
        val defaultValue = parametersByName.get(param.name).flatMap(_.defaultValue).map(_.calculate( calleeContext ))
        if (defaultValue.isDefined) {
          argumentValues.addBinding(param.name, defaultValue.get)
        }
        else {
          throw new RunError("Required parameter '"+param.name.name+"' not provided.", callLocation)
        }
      }
    }

    invoke(argumentValues)
  }


  def invoke(argumentValues: DynamicContext): Value

}