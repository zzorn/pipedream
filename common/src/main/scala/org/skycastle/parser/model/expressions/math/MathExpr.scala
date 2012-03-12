package org.skycastle.parser.model.expressions.math

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.Context


abstract case class MathExpr(op: String) extends Expr {
  val a: Expr
  val b: Expr

  def resultType = a.resultType

  def output(s: StringBuilder, indent: Int) {
    a.output(s, indent)
    s.append(" ")
    s.append(op)
    s.append(" ")
    b.output(s, indent)
  }
}

case class AddExpr(a: Expr, b: Expr) extends MathExpr("+") {
  def calculation: Context => AnyRef = {
    null
  }
}

case class SubExpr(a: Expr, b: Expr) extends MathExpr("-") {
  def calculation: Context => AnyRef = {
    null
  }
}

case class MulExpr(a: Expr, b: Expr) extends MathExpr("*") {
  def calculation: Context => AnyRef = {
    null
  }
}

case class DivExpr(a: Expr, b: Expr) extends MathExpr("/") {
  def calculation: Context => AnyRef = {
    null
  }
}

case class PowExpr(a: Expr, b: Expr) extends MathExpr("^") {
  def calculation: Context => AnyRef = {
    null
  }
}

case class NegExpr(a: Expr) extends Expr {
  def resultType = a.resultType

  def calculation: Context => AnyRef = {
    null
  }

  def output(s: StringBuilder, indent: Int) {
    s.append("-")
    a.output(s, indent)
  }
}



