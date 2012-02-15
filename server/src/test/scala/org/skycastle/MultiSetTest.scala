package org.skycastle

import java.lang.IllegalArgumentException
import org.scalatest.FunSuite
import org.scalaprops.exporter.{BeanExporter, JsonBeanExporter}
import org.scalaprops.parser.{JsonBeanParser, BeanParser}
import util.MultiSet

class MultiSetTest extends FunSuite {

  test("Multiset") {
    val ms = new MultiSet[String]()
    assert(ms.isEmpty)
    ms.increase("foo")
    assert(!ms.isEmpty)
    assert(ms("foo") == 1)
    println("No assertions, test ok")
  }

  test("Addition") {

    val ms = new MultiSet[String]()

    ms.isEmpty === true

    ms.increase("foo")

    ms.isEmpty === false
    ms("foo") === 1

  }
}
