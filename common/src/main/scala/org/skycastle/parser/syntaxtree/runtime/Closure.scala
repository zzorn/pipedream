package org.skycastle.parser.syntaxtree.runtime

import java.util.ArrayList
import org.skycastle.parser.model.SyntaxError
import org.skycastle.parser.syntaxtree.{SyntaxError, Parameter, Expr}
import org.skycastle.parser.{RunError, ChainedContext}


/**
 *
 */
case class Closure(localContext: DynamicContext, function: Expr, parameters: List[Parameter]) extends Value with Invokable {

  def invoke(parameterValues: MutableDynamicContext): Value = {

    // Calculate default values
    // Check that all parameters had some argument or default argument provided
    parameters foreach {param =>
      if (!parameterValues.hasBinding(param.name)){
        if (param.defaultValue.isDefined) {
          val defaultValue = param.defaultValue.get.calculate(localContext)

        }
        else {
          throw new RunError("Required parameter '"+param.name.name+"' not provided.")
        }

        val defaultValue = parametersByName.get(param.name).flatMap(_.defaultValue).map(_.calculate(localContext))
        if (defaultValue.isDefined) {
          parameters.addBinding(param.name, defaultValue.get)
        }
        else {
          throw new RunError("Required parameter '"+param.name.name+"' not provided.", callLocation)
        }
      }
    }


    val context = ChainedContext(parameterValues, localContext)

  }
  
}