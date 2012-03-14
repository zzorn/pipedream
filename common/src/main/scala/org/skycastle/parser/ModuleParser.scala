package org.skycastle.parser

import java.io.Reader
import model._
import defs.{Parameter, ValDef, Def, FunDef}
import expressions._
import model.expressions.math._
import model.expressions.bool._
import module.{Import, Module}
import refs.{Arg, Call, Ref}
import scala.util.parsing.combinator.JavaTokenParsers
import scala.Predef._
import util.parsing.combinator.syntactical.StandardTokenParsers
import util.parsing.input.{PagedSeqReader, CharSequenceReader}
import collection.immutable.PagedSeq
import org.skycastle.utils.LanguageParser

/**
 * Parses modules from input files
 */
class ModuleParser(beanFactory: BeanFactory) extends LanguageParser[Module] {

  val FUN = registerKeyword("fun")
  val VAL = registerKeyword("val")
  val IMPORT = registerKeyword("import")
  val RETURN = registerKeyword("return")
  val AND = registerKeyword("and")
  val OR = registerKeyword("or")
  val XOR = registerKeyword("xor")
  val NOT = registerKeyword("not")
  val FALSE = registerKeyword("false")
  val TRUE = registerKeyword("true")

  registerDelimiters(
    "=", ",", ":", ";",
    "(", ")", "[", "]", "{", "}",
    "->", "=>",
    "+","-", "*", "/", "^",
    "<",">", "<=", ">=", "!=", "==", "<>"
  )

  // Module
  def rootParser = module
  private lazy val module: PackratParser[Module] =
    imports ~ definitions ^^
      { case imps ~ defs =>
        Module(imps, defs) }

  // Imports
  private lazy val imports: PackratParser[List[Import]] = repsep(imp, opt(";"))
  private lazy val imp: PackratParser[Import] =
    IMPORT ~> ident ^^
      { case i => Import(i) }

  // Definitions
  private lazy val definitions: PackratParser[List[Def]] = rep(definition)
  private lazy val definition: PackratParser[Def] = (funDef | valDef) <~ opt(";")

  // Value definition
  private lazy val valDef: PackratParser[ValDef] =
    VAL ~> ident ~ opt(typeTag) ~ ("=" ~> expression) ^^
      {case name ~ t ~ exp =>
        ValDef(Symbol(name), if (t.isDefined) t.get else exp.resultType, exp)}

  // Function definition
  private lazy val funDef: PackratParser[FunDef] =
    FUN ~> ident ~ parameterList ~ opt(typeTag) ~ ("=" ~> expression | block) ^^
      {case name ~ params ~ t ~ exp =>
        FunDef(Symbol(name), params, if (t.isDefined) t.get else exp.resultType, exp)}

  private lazy val parameterList: PackratParser[List[Parameter]] =
    "(" ~> repsep(parameter, ",") <~")"

  private lazy val parameter: PackratParser[Parameter] =
    parameterWithDefault | parameterWithType

  private lazy val parameterWithType: PackratParser[Parameter] =
    ident ~ typeTag ~ opt ("=" ~> expression) ^^
    {case name ~ t ~ defaultExp =>
     new Parameter(Symbol(name), t, defaultExp)}

  private lazy val parameterWithDefault: PackratParser[Parameter] =
    ident ~ ("=" ~> expression) ^^
    {case name ~ defaultExp =>
     new Parameter(Symbol(name), defaultExp.resultType, Some(defaultExp))}



  // Types
  private lazy val typeTag: PackratParser[TypeDef] = ":" ~> typeDesc

  private lazy val typeDesc: PackratParser[TypeDef] = funType | "(" ~> typeDesc <~ ")" | simpleType

  private lazy val simpleType: PackratParser[SimpleType] =
    ident ^^ { case typeName => SimpleType(Symbol(typeName), beanFactory.typeForName(typeName)) }

  private lazy val funType: PackratParser[FunType] = singleParamFunType | multiParamFunType

  private lazy val singleParamFunType: PackratParser[FunType] =
    typeDesc ~ "=>" ~ typeDesc ^^
      {case paramType ~ arrow ~ resultType =>
        FunType(List(paramType), resultType)}

  private lazy val multiParamFunType: PackratParser[FunType] =
    "(" ~ repsep (typeDesc, ",") ~ ")" ~ "=>" ~ typeDesc ^^
      {case lp ~ paramTypes ~ rp ~ arrow ~ resultType =>
        FunType(paramTypes, resultType)}



  // Expressions
  private lazy val expression: PackratParser[Expr] = (
        funExpr
      | mathExpr
      | list
      | quotedString ^^ (x => StringExpr(x))
      | "null" ^^ (x => NullExpr)
      | "true" ^^ (x => TrueExpr)
      | "false" ^^ (x => FalseExpr)
    )


  // Block expression
  private lazy val block: PackratParser[Block] =
    "{"~> definitions ~ (RETURN ~> expression) <~ "}" ^^
      { case defs ~ exp =>
        Block(defs, exp) }


  // List expression
  private lazy val list: PackratParser[ListExpr] =
    "["~> repsep(expression, opt(",")) <~"]" ^^
      {case values => ListExpr(values)}


  // Function expression
  private lazy val funExpr: PackratParser[FunExpr] =
    funExprParameterList ~ opt(typeTag) ~ ("=>" ~> expression) ^^
      {case params ~ t ~ exp =>
        FunExpr(params, if (t.isDefined) t.get else exp.resultType, exp)}

  private lazy val funExprParameterList: PackratParser[List[Parameter]] = (
       funExprParameter ^^ {case p => List(p)}
      | "(" ~> repsep(funExprParameter, ",") <~")"
    )

  private lazy val funExprParameter: PackratParser[Parameter] = parameterWithDefault | parameterWithType | funExprParameterWithoutTypeOrDefault
  private lazy val funExprParameterWithoutTypeOrDefault: PackratParser[Parameter] =
    ident ^^ {case name => new Parameter(Symbol(name), null, None)}



  // Value references
  private lazy val ref: PackratParser[Ref] =
    ident ^^ {case refName => Ref(Symbol(refName))}


  // Function calls
  private lazy val call: PackratParser[Call] =
    ident ~ "(" ~ arguments ~ ")" ^^
      {case name ~ "(" ~ args ~ ")" =>
        Call(Symbol(name), args)}

  private lazy val arguments: PackratParser[List[Arg]] =
    repsep(namedArgument | unnamedArgument, opt(","))

  private lazy val namedArgument: PackratParser[Arg] =
    ident ~ "=" ~ expression ^^
      {case name ~ "=" ~ expr =>
        Arg(Some(Symbol(name)), expr) }


  private lazy val unnamedArgument: PackratParser[Arg] =
    expression ^^ {case expr => new Arg(None.asInstanceOf[Option[Symbol]], expr) }



  // Math expressions
  private lazy val mathExpr: PackratParser[Expr] =
    term ~ rep(("+" | "-") ~ term) ^^ { case first ~ rest => processOperations(first, rest)}

  private lazy val term: PackratParser[Expr] =
    factor ~ rep(("*" | "/") ~ factor) ^^ { case first ~ rest => processOperations(first, rest)}

  private lazy val factor: PackratParser[Expr] =
    factor2 ~ rep("^" ~ factor2) ^^ { case first ~ rest => processOperations(first, rest)}

  private lazy val factor2: PackratParser[Expr] = (
        "-" ~> factor3 ^^ {case a => NegExpr(a)}
      | factor3
  )

  private lazy val factor3: PackratParser[Expr] = (
        block
      | call
      | ref
      | "(" ~> mathExpr <~")" ^^ {case x: Expr => Parens(x)}
      | doubleNumber ^^ (x => DoubleExpr(x))
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




}
