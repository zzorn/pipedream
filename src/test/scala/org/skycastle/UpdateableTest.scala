package org.skycastle

import utils.Updateable
import org.scalatest.FunSuite

class UpdateableTest extends FunSuite {

  case class TestBean(var aa: Int, var bb: TestBean) extends Updateable {
    var cc: String = ""
  }

  test("Updating bean") {
    val t = TestBean(1, TestBean(2, null))
    t.cc = "foo"

    t.update(Map('aa -> 10, 'bb -> Map('aa -> 20, 'cc -> "bar")))

    assert(t.aa === 10)
    assert(t.cc === "foo")
    assert(t.bb.aa === 20)
    assert(t.bb.cc === "bar")
    assert(t.bb.bb === null)
  }

}

