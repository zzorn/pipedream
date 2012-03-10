package org.skycastle.parser

import model.Module
import org.scalatest.FunSuite
import java.io.StringReader

/**
 *
 */
class ParserTest extends FunSuite {

  test("Parse") {
    val factory = new BeanFactory()
    val parser = new BeanParser(factory)
    val testString =
      """
      fun foo(a: double, b = 1) = a * b * 4 / 3 + 1 - (a + b) * 3
      fun bar(f(t: double)= t^(t*2)) = foo(f(1), b = f(2))
      fun zap(g(x: double): double) = bar(g) + bar( (x) = 2^x * x*3 + 1/x + g(x) + g(4) + 3.123E-12 )

      fun tree(height = 1): Model = {
        fun rootHeight = height / 2
        root(rootHeight)
        many(branch)
        many([leaf1, leaf2, leaf3])
      }
      """
    val module: Module = parser.parse(new StringReader(testString), "test input")

    println(testString)

    assert(module.functions.size === 4)

    val s = module.toString()
    println(s)

    val module2: Module = parser.parse(new StringReader(s), "outputted test input")
    val s2 = module2.toString()

    println(s2)

    assert(s === s2)
  }

}
