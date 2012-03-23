package org.skycastle.parser.syntaxtree.runtime

import org.skycastle.parser.syntaxtree.{Expr, DynamicContext}

/**
 *
 */
case class Closure(context: DynamicContext, function: Expr) extends Value {

}