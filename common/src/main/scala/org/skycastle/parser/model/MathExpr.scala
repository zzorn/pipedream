package org.skycastle.parser.model


abstract case class MathExpr(op: String) extends Expression {
  val a: Expression
  val b: Expression

  def output(s: StringBuilder, indent: Int) {
    a.output(s, indent)
    s.append(" ")
    s.append(op)
    s.append(" ")
    b.output(s, indent)
  }
}

case class AddExpr(a: Expression, b: Expression) extends MathExpr("+") {
  def resultType = a.resultType
  def calculation: Context => AnyRef = {
    null
  }
}

case class SubExpr(a: Expression, b: Expression) extends MathExpr("-") {
  def resultType = a.resultType
  def calculation: Context => AnyRef = {
    null
  }
}

case class MulExpr(a: Expression, b: Expression) extends MathExpr("*") {
  def resultType = a.resultType
  def calculation: Context => AnyRef = {
    null
  }
}

case class DivExpr(a: Expression, b: Expression) extends MathExpr("/") {
  def resultType = a.resultType
  def calculation: Context => AnyRef = {
    null
  }
}

case class PowExpr(a: Expression, b: Expression) extends MathExpr("^") {
  def resultType = a.resultType
  def calculation: Context => AnyRef = {
    null
  }
}

case class NegExpr(a: Expression) extends Expression {
  def resultType = a.resultType
  def calculation: Context => AnyRef = {
    null
  }

  def output(s: StringBuilder, indent: Int) {
    s.append("-")
    a.output(s, indent)
  }
}



