package org.skycastle.parser.model

import defs.Parameter
import expressions.Expr
import refs.Arg
import org.skycastle.parser.{RunError, ChainedContext, Context}

/**
 *
 */
case class Closure(context: Context, parameters: List[Parameter], expression: Expr, functionType: TypeDef) extends Value {

  lazy val parametersByName: Map[Symbol, Parameter] = parameters.map(p => p.name -> p).toMap

  /**
   * Invokes the closure from the specified caller context, with the specified argument list.
   */
  def invokeWithCallerContext(arguments: List[Arg], callerContext: Context): Value = {

    val argumentValues = new MutableContext()

    // Bind argument names to argument values
    // Check that all arguments map to some parameter
    var index = 0
    arguments foreach {a: Arg =>
      if (a.paramName.isDefined) {
        if (parametersByName.contains(a.paramName.get))
          argumentValues.addBinding(a.paramName.get, a.value.calculate(context))
        else
          throw new RunError("Invoked function has no parameter named '" + a.paramName.get.name + "'.")
      }
      else {
        if (index < parameters.size) {
          argumentValues.addBinding(parameters(index).name, a.value.calculate(context))
          index += 1
        }
        else {
          throw new RunError("Too many paramters for invoked function.")
        }
      }
    }

    // Calculate default values
    // Check that all parameters had some argument or default argument provided
    parameters foreach {param =>
      if (!argumentValues.hasBinding(param.name)){
        val defaultValue = parametersByName.get(param.name).flatMap(_.defaultValue).map(_.calculate(callerContext))
        if (defaultValue.isDefined) {
          argumentValues.addBinding(param.name, defaultValue.get)
        }
        else {
          throw new RunError("Required parameter '"+param.name.name+"' not provided.")
        }
      }
    }

    // Run the closure with the calculated parameter values
    invoke(argumentValues)
  }


  /**
   * Invokes the closure with calculated parameter values.
   */
  def invoke(args: Context): Value = {
    expression.calculate(ChainedContext(args, context))
  }

  def unwrappedValue = this
}