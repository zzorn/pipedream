package org.skycastle.parser

import java.io.Reader
import model._
import defs.{Parameter, ValDef, Def, FunDef}
import expressions._
import model.expressions.math._
import model.expressions.bool._
import module.{Import, Module}
import refs.{NamedArg, Call, Ref}
import scala.util.parsing.combinator.JavaTokenParsers
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

  // Module
  private lazy val module: Parser[Module] =
    imports ~ definitions ^^
      { case imps ~ defs =>
        Module(imps, defs) }

  // Imports
  private lazy val imports: Parser[List[Import]] = repsep(imp, opt(";"))
  private lazy val imp: Parser[Import] =
    "import" ~> ident ^^
      { case i => Import(i) }

  // Definitions
  private lazy val definitions: Parser[List[Def]] = repsep(definition, opt(";"))
  private lazy val definition: Parser[Def] = funDef | valDef

  // Value definition
  private lazy val valDef: Parser[ValDef] =
    "val" ~> ident ~ opt(typeTag) ~ ("=" ~> expression) ^^
      {case name ~ t ~ exp =>
        ValDef(Symbol(name), if (t.isDefined) t.get else exp.resultType, exp)}

  // Function definition
  private lazy val funDef: Parser[FunDef] =
    "fun" ~> ident ~ parameterList ~ opt(typeTag) ~ ("=" ~> expression | block) ^^
      {case name ~ params ~ t ~ exp =>
        FunDef(Symbol(name), params, if (t.isDefined) t.get else exp.resultType, exp)}

  private lazy val parameterList: Parser[List[Parameter]] =
    opt("(" ~> repsep(parameter, opt(",")) <~")") ^^
    { case params => if (params.isEmpty) Nil else params.get }

  private lazy val parameter: Parser[Parameter] =
    parameterWithDefault | parameterWithType

  private lazy val parameterWithType: Parser[Parameter] =
    ident ~ typeTag ~ opt ("=" ~> expression) ^^
    {case name ~ t ~ defaultExp =>
     new Parameter(Symbol(name), t, defaultExp)}

  private lazy val parameterWithDefault: Parser[Parameter] =
    ident ~ ("=" ~> expression) ^^
    {case name ~ defaultExp =>
     new Parameter(Symbol(name), defaultExp.resultType, Some(defaultExp))}


  // Types
  private lazy val typeTag: Parser[TypeDef] = ":" ~> typeDesc

  private lazy val typeDesc: Parser[TypeDef] = "(" ~> typeDesc <~ ")" | simpleType | funType

  private lazy val simpleType: Parser[SimpleType] =
    ident ^^ { case typeName => SimpleType(beanFactory.typeForName(typeName)) }

  private lazy val funType: Parser[FunType] = singleParamFunType | multiParamFunType

  private lazy val singleParamFunType: Parser[FunType] =
    opt("(") ~ typeDesc ~ opt(")") ~ "=>" ~ typeDesc ^^
    {case lp ~ paramType ~ rp ~ arrow ~ resultType => FunType(List(paramType), resultType)}

  private lazy val multiParamFunType: Parser[FunType] =
    "(" ~ repsep (typeDesc, opt(",")) ~ ")" ~ "=>" ~ typeDesc ^^
    {case lp ~ paramTypes ~ rp ~ arrow ~ resultType => FunType(paramTypes, resultType)}



  // Expressions
  private lazy val expression: Parser[Expr] = (
        funExpr
      | mathExpr
      | list
      | stringLiteral ^^ (x => StringExpr(stripQuotes(x)))
      | "null" ^^ (x => NullExpr)
      | "true" ^^ (x => TrueExpr)
      | "false" ^^ (x => FalseExpr)
    )


  // Block expression
  private lazy val block: Parser[Block] =
    "{"~> definitions ~ expression <~ "}" ^^
      { case defs ~ exp =>
        Block(defs, exp) }


  // List expression
  private lazy val list: Parser[ListExpr] =
    "["~> repsep(expression, opt(",")) <~"]" ^^
      {case values => ListExpr(values)}


  // Function expression
  private lazy val funExpr: Parser[FunExpr] =
    parameterList ~ opt(typeTag) ~ ("=>" ~> expression) ^^
      {case params ~ t ~ exp =>
        FunExpr(params, if (t.isDefined) t.get else exp.resultType, exp)}



  // Value references
  private lazy val ref: Parser[Ref] =
    ident ^^ {case refName => Ref(Symbol(refName))}


  // Function calls
  private lazy val call: Parser[Call] =
    ident ~ "(" ~ unnamedArguments ~ opt(",") ~ namedArguments ~ ")" ^^
      {case name ~ "(" ~ unnamedArgs ~ c ~ namedArgs ~ ")" =>
        Call(Symbol(name), unnamedArgs, namedArgs)}

  private lazy val unnamedArguments: Parser[List[Expr]] =
    repsep(expression, ",")

  private lazy val namedArguments: Parser[List[NamedArg]] =
    repsep(namedArgument, ",")

  private lazy val namedArgument: Parser[NamedArg] =
    ident ~ "=" ~ expression ^^
      {case name ~ "=" ~ expr =>
        NamedArg(Symbol(name), expr) }



  // Math expressions
  private lazy val mathExpr: Parser[Expr] =
    term ~ rep(("+" | "-") ~ term) ^^ { case first ~ rest => processOperations(first, rest)}

  private lazy val term: Parser[Expr] =
    factor ~ rep(("*" | "/") ~ factor) ^^ { case first ~ rest => processOperations(first, rest)}

  private lazy val factor: Parser[Expr] =
    factor2 ~ rep("^" ~ factor2) ^^ { case first ~ rest => processOperations(first, rest)}

  private lazy val factor2: Parser[Expr] = (
        "-" ~> factor3 ^^ {case a => NegExpr(a)}
      | factor3
  )

  private lazy val factor3: Parser[Expr] = (
        block
      | call
      | ref
      | "(" ~> mathExpr <~")" ^^ {case x: Expr => Parens(x)}
      | floatingPointNumber ^^ (x => DoubleExpr(stringToDouble(x)))
  )



  // Helper methods
  private def processOperations(first: Expr, rest: List[~[String, Expr]]):Expr = {
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
