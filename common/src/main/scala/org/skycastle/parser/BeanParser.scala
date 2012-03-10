package org.skycastle.parser

import java.io.Reader
import model._
import scala.util.parsing.combinator.JavaTokenParsers
import serializers.Serializers
import scala.Predef._

/**
 * Parses Scala beans from input files
 */
class BeanParser(beanFactory: BeanFactory) extends JavaTokenParsers {

  def parse(reader: Reader, sourceName: String): Module = {

    parseAll(module, reader) match {
      case s: Success[Module] =>  s.result
      case f: NoSuccess=> throw new ParseError(f.msg, f.next.pos, sourceName)
    }

  }

  private lazy val module: Parser[Module] = imports ~ repsep(fun, opt(";")) ^^ { case imps ~ funs => Module(imps, funs) }
  private lazy val imports: Parser[List[Import]] = repsep(imp, opt(";"))
  private lazy val imp: Parser[Import] = "import" ~> ident ^^ { case i => Import(i) }

  private lazy val fun: Parser[Definition] = "fun" ~> definition ^^ { case d => d }

  private lazy val definition: Parser[Definition] = ident ~ parameterList ~ opt(":" ~> kind) ~ "=" ~ expression ^^
    {case name ~ params ~ t ~ "=" ~ exp => Definition(Symbol(name), params, if (t.isDefined) t.get else exp.resultType, exp)}

  private lazy val parameterList: Parser[List[Parameter]] =
    opt("(" ~> repsep(parameter, opt(",")) <~")") ^^ { case params => if (params.isEmpty) Nil else params.get }

  private lazy val parameter: Parser[Parameter] = parameterWithDefault | parameterWithType

  private lazy val parameterWithType: Parser[Parameter] = ident ~ parameterList ~ (":" ~> kind) ~ opt ("=" ~> expression) ^^
    {case name ~ params ~ t ~ defaultExp => new Parameter(Symbol(name), params, t, defaultExp)}

  private lazy val parameterWithDefault: Parser[Parameter] = ident ~ parameterList ~ ("=" ~> expression) ^^
    {case name ~ params ~ defaultExp => new Parameter(Symbol(name), params, defaultExp.resultType, Some(defaultExp))}


  private lazy val kind: Parser[Class[_]] =
    ident ^^ ( beanFactory.typeForName(_) )

  private lazy val expression: Parser[Expression] = (
        block
      | mathExpr
      | list
      | stringLiteral ^^ (x => StringExpr(stripQuotes(x)))
      | "null" ^^ (x => NullExpr)
      | "true" ^^ (x => TrueExpr)
      | "false" ^^ (x => FalseExpr)
    )

  private lazy val mathExpr: Parser[Expression] =
    term ~ rep(("+" | "-") ~ term) ^^ { case first ~ rest => processOperations(first, rest)}

  private lazy val term: Parser[Expression] =
    factor ~ rep(("*" | "/") ~ factor) ^^ { case first ~ rest => processOperations(first, rest)}

  private lazy val factor: Parser[Expression] =
    factor2 ~ rep("^" ~ factor2) ^^ { case first ~ rest => processOperations(first, rest)}

  private lazy val factor2: Parser[Expression] = (
        "-" ~> factor3 ^^ {case a => NegExpr(a)}
      | factor3
  )

  private lazy val factor3: Parser[Expression] = (
        call
      | "(" ~> mathExpr <~")" ^^ {case x: Expression => Parens(x)}
      | floatingPointNumber ^^ (x => DoubleExpr(stringToDouble(x)))
  )


  private lazy val block: Parser[Block] =
    "{"~> repsep(fun, opt(";")) ~ repsep(expression, opt(",")) <~ "}" ^^ { case funs ~ expressionList =>
      Block(funs, expressionList) }

  private lazy val list: Parser[ListExpr] = "["~> repsep(expression, opt(",")) <~"]" ^^ {case values => ListExpr(values)}

  private lazy val call: Parser[Call] =
    ident ~ opt("(" ~> repsep(callParam, opt(",")) <~ ")") ^^ {case name ~ params =>
      Call(Symbol(name), if (params.isDefined) params.get else Nil)}

  private lazy val callParam: Parser[CallParam] =
    opt(opt(ident) ~ callParameterList <~ "=") ~ expression ^^ {
      case namedParam ~ value =>
        CallParam(if (namedParam.isDefined && namedParam.get._1.isDefined) Some(Symbol(namedParam.get._1.get)) else None,
                  if (namedParam.isDefined) namedParam.get._2 else Nil,
                  value) }


  private lazy val callParameterList: Parser[List[CallParam]] =
    opt("(" ~> repsep(callParam, opt(",")) <~")") ^^ { case params => if (params.isEmpty) Nil else params.get }




  private def processOperations(first: Expression, rest: List[~[String, Expression]]):Expression = {
    if(rest.isEmpty) first
    else {
      val next = rest.head
      val opChar = next._1
      val other = processOperations(next._2, rest.tail)
      opChar match {
        case "+" => AddExpr(first, other)
        case "-" => SubExpr(first, other)
        case "*" => MulExpr(first, other)
        case "/" => DivExpr(first, other)
        case "^" => PowExpr(first, other)
      }
    }
  }

  private def stripQuotes(x: String): String = x.substring(1, x.length - 1)

  private def stringToDouble(s: String): Double = new java.lang.Double(s)

  private def stringToNumber(s: String): AnyRef = {
    if (s.contains(".") ||
      s.contains("e") ||
      s.contains("E")) new java.lang.Double(s)
    else if (s.length <= 9) new java.lang.Integer(s)
    else new java.lang.Long(s)
  }


}
