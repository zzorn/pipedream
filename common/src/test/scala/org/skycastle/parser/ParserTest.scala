package org.skycastle.parser

import model.defs.FunDef
import model.expressions.{Num, Expr}
import model.expressions.math.Num
import model.module.Module
import model.refs.Arg
import org.scalatest.FunSuite
import java.io.{File, StringReader}
import org.scalatest.Assertions._

/**
 *
 */
class ParserTest extends FunSuite {

  def parseCode(flowCode: String): String = {
    val factory = new BeanFactory()
    val parser = new ModuleParser(factory)
    val module: Module = parser.parseString(flowCode, "test input")
    module.generateJavaCode()
  }

  def shouldParseTo(flowCode: String, expectedJavaCode: String) {
    assert(expectedJavaCode === parseCode(flowCode))
  }

  test("Empty module") {
    shouldParseTo(
    """
      module Foo {
      }
    """,
    """
      public class Foo {

      }
    """
    )
  }
/*
  test("Import") {
    shouldParseTo(
    """
    """,
    """
    """
    )
  }

  test("Function") {
    shouldParseTo(
    """
    """,
    """
    """
    )
  }
*/
  /*
  test ("Load packages") {
    val loader = new ModuleLoader()
    val root = loader.loadRootModule(new File("assets/testpackage"))
    val s = root.toString
    println(s)

    assert(root.getMemberByPath(List('skycastle, 'utils, 'MathUtils, 'lerp)).isDefined)

    val module2: Module = loader.parser.parse(new StringReader(s), "outputted test input")
    val s2 = module2.toString()
    assert(s === s2)
  }

  test ("Invoke Function") {
    val loader = new ModuleLoader()
    val root = loader.loadRootModule(new File("assets/testpackage"))

    val lerp= root.getMemberByPath(List('skycastle, 'utils, 'MathUtils, 'lerp)).asInstanceOf[Option[FunDef]]
    val result: Expr = lerp.get.invoke(List(Arg(Num(10.0)), Arg(Num(20.0)), Arg(Num(0.5), Some('t))))
    assert(result === Num(15.0))

  }
*/
}
