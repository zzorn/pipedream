package org.skycastle.scripting

import janino.JaninoScriptingService
import org.scalatest.FunSuite

/**
 *
 */
class JaninoScriptingServiceTest extends FunSuite {

  test("Compiling and running script") {

    val scriptingService = new JaninoScriptingService()

    val script = scriptingService.getScript("test/testScript.java")

    val result = script.invoke(Map('x -> 3))

    assert(9 === result)

  }
}
