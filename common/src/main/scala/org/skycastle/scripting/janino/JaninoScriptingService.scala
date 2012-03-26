package org.skycastle.scripting.janino

import java.security.SecureClassLoader
import net.sf.cglib.transform.AbstractClassLoader
import org.skycastle.scripting.{SandboxedClassLoader, Script, ScriptingService}
import collection.immutable.Set
import org.codehaus.janino.{WarningHandler, CachingJavaSourceClassLoader, JavaSourceClassLoader}
import org.codehaus.commons.compiler.Location
import java.lang.Class
import org.codehaus.janino.util.resource._
import java.io.{StringReader, File}
import org.apache.commons.io.input.ReaderInputStream
import java.util.{ArrayList, HashMap, Arrays}

/**
 *
 */
class JaninoScriptingService(allowedClasses: Set[String] = Set(), sourceFinder: ResourceFinder = null) extends ScriptingService {

  private var loader: JavaSourceClassLoader = null
  private var scriptClasses: Map[String, Class[Script]] = Map()
  private val javaCode: HashMap[String, Resource] = new HashMap[String, Resource]()

  setup()
  
  private def setup() {
    val sandboxedLoader = new SandboxedClassLoader(allowedClasses)

    val localCodeFinder: ResourceFinder = new ResourceFinder {
      def findResource(resourceName: String): Resource = javaCode.get(resourceName)
    }

    val resourceFinders = new ArrayList[ResourceFinder]
    resourceFinders.add(localCodeFinder)
    if (sourceFinder != null) resourceFinders.add(sourceFinder)
    val resourceFinder: ResourceFinder = new MultiResourceFinder(resourceFinders)

    // TODO: Handle resources stored in local jar -> find using getClass.getResource

    loader = new JavaSourceClassLoader(sandboxedLoader, resourceFinder, null)
    loader.setWarningHandler(new WarningHandler {
      def handleWarning(handle: String, message: String, optionalLocation: Location) {
        // TODO: Gather errors, display in editor or such, or log properly
        println("WARNING: " + handle + " " + message + " at " + optionalLocation)
      }
    })
  }

  def addScript(path: String, code: String) {
    val lastModifiedTime = System.currentTimeMillis()
    val resource = new Resource {
      def open() = new ReaderInputStream(new StringReader(code))
      def getFileName = path
      def lastModified() = lastModifiedTime
    } 
    javaCode.put(path, resource)
  }
  
  def registerScriptClass(path: String, scriptClass: Class[Script]) {
    scriptClasses += path -> scriptClass
  }

  def getScriptClass(path: String): Class[Script] = {
    // Get or load the script class
    scriptClasses.get(path) match {
      case Some(x: Class[Script]) => x
      case None =>
        val loadedClass = loader.loadClass(path)

        if (!classOf[Script].isAssignableFrom(loadedClass)) {
          throw new IllegalStateException("Script with path '"+path+"' does not implement the Script interface.")
        }
        else {
          val scriptClass = loadedClass.asInstanceOf[Class[Script]]
          registerScriptClass(path, scriptClass)
          scriptClass
        }
    }
  }
  
  def createScript(path: String): Script = {

    // Create and return a new instance
    getScriptClass(path).newInstance()
  }

}
