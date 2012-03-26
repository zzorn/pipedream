package org.skycastle.scripting

import janino.JaninoScriptingService
import org.scalatest.FunSuite

/**
 *
 */
class JaninoScriptingServiceTest extends FunSuite {

  test("Compiling and running script") {

    val scriptingService = new JaninoScriptingService(Set("org.skycastle.scripting.Script", "test.TestScript"))

    scriptingService.addScript("test/TestScript.java",
      """
      package test;

      import org.skycastle.scripting.Script;
      import java.lang.Object;
      import java.lang.Integer;

      public class TestScript implements Script {

          public Object invoke(Object[] parameters) {
              int x = (Integer)parameters[0];
              return x * x;
          }

      }
      """
    )

    val script = scriptingService.createScript("test.TestScript")

    val result = script.invoke(new Integer(3))

    assert(9 === result)

  }
}
