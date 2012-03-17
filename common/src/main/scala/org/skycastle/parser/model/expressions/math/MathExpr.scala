package org.skycastle.parser.model.expressions.math

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.ResolverContext
import org.skycastle.parser.model.{SyntaxNode, TypeDef, Context}


abstract case class MathExpr(op: String) extends Expr {
  val a: Expr
  val b: Expr

  def output(s: StringBuilder, indent: Int) {
    a.output(s, indent)
    s.append(" ")
    s.append(op)
    s.append(" ")
    b.output(s, indent)
  }

  override def subNodes = List(a, b, valueType).iterator

  override def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef = {
    if (a.valueType == null) null
    else a.valueType(visitedNodes).mostSpecificCommonType(b.valueType(visitedNodes))
  }
}

case class AddExpr(a: Expr, b: Expr) extends MathExpr("+") {
}

case class SubExpr(a: Expr, b: Expr) extends MathExpr("-") {
}

case class MulExpr(a: Expr, b: Expr) extends MathExpr("*") {
}

case class DivExpr(a: Expr, b: Expr) extends MathExpr("/") {
}

case class PowExpr(a: Expr, b: Expr) extends MathExpr("^") {
}

case class NegExpr(a: Expr) extends Expr {

  def output(s: StringBuilder, indent: Int) {
    s.append("-")
    a.output(s, indent)
  }

  protected def determineValueType(visitedNodes: Set[SyntaxNode]) = a.valueType(visitedNodes)
}



