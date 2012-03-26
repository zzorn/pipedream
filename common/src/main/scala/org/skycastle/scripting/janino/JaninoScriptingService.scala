package org.skycastle.scripting.janino

import org.skycastle.scripting.{Script, ScriptingService}
import org.codehaus.janino.{CachingJavaSourceClassLoader, JavaSourceClassLoader}
import java.security.SecureClassLoader

/**
 *
 */
class JaninoScriptingService extends ScriptingService {

  private var loader: JavaSourceClassLoader = new CachingJavaSourceClassLoader(
  new
  )

  private var code = Map[String, Script]

  def registerScript(path: String, code: String) {

  }

  def getScript(path: String): Script = {

  }

}
