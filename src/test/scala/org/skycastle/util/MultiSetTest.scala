package org.skycastle.util

import java.lang.IllegalArgumentException
import org.scalatest.FunSuite
import org.scalaprops.exporter.{BeanExporter, JsonBeanExporter}
import org.scalaprops.parser.{JsonBeanParser, BeanParser}

class MultiSetTest extends FunSuite {

  test("Addition") {

    val ms = new MultiSet[String]()

    ms.isEmpty === true

    ms.increase("foo")

    ms.isEmpty === false
    ms("foo") === 1

  }

}
