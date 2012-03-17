package org.skycastle.parser

import model.defs.FunDef
import model.expressions.Expr
import model.expressions.math.Num
import model.module.Module
import model.refs.Arg
import org.scalatest.FunSuite
import java.io.{File, StringReader}

/**
 *
 */
class ParserTest extends FunSuite {

  test("Parse") {
    val factory = new BeanFactory()
    val parser = new ModuleParser(factory)
    val testString =
      """
      module ParseTest {
        fun foo(a, b = 1) = a * -b * 4.3 / 3 + 1 - -(-a + -b) * 3 + zomg(x = {fun boo() = 5 return -boo() }) * 4
        fun bar(f = (t: Num) => t^(t*2) )  { return f(1) + f(2, 3) + f(aasd = 1) + f(ae=1, basd=2) + f(1,b=3) + foo(f(1), b = f(2)) }
        fun zap(g: (Num) -> Num) = bar(g 3 4, d, bar=3 foo=4 5 g-h-1 7-6) + bar( f = x => 2^x * x*3 + 1/x + g(x) + g(4) + 3.123E-12 )

        fun tree(height = 1, apples: List[Model]): Model = {
          fun leafCalculator(x: Num): Leaf = Leaf(2, 45*x)
          val rootHeight = height / 2
          return [
            root(rootHeight)
            apples.pickRandom(seed=2)
            many(branch)
            many([leafCalculator(1), leafCalculator(2), leafCalculator(3)])
          ]
        }
      }
      """
    val module: Module = parser.parseString(testString, "test input")

    println(testString)

    assert(module.definitions.size === 4)

    val s = module.toString()
    println(s)

    val module2: Module = parser.parse(new StringReader(s), "outputted test input")
    val s2 = module2.toString()

    println(s2)

    assert(s === s2)
  }
  
  test ("Load packages") {
    val loader = new ModuleLoader()
    val root = loader.loadRootModule(new File("assets/testpackage"))
    println(root)

    assert(root.getMemberByPath(List('skycastle, 'utils, 'MathUtils, 'lerp)).isDefined)

  }

  test ("Invoke Function") {
    val loader = new ModuleLoader()
    val root = loader.loadRootModule(new File("assets/testpackage"))

    val lerp= root.getMemberByPath(List('skycastle, 'utils, 'MathUtils, 'lerp)).asInstanceOf[Option[FunDef]]
    val result: Expr = lerp.get.invoke(List(Arg(Num(10.0)), Arg(Num(20.0)), Arg(Num(0.5), Some('t))))
    assert(result === Num(15.0))

  }

}
