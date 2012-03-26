package org.skycastle.scripting

import java.security.SecureClassLoader
import java.net.URL
import java.lang.Class
import java.io.InputStream

/**
 * A ClassLoader that only supports loading a specified set of whitelisted classes.
 */
final class SandboxedClassLoader(allowedClasses: Set[String]) extends ClassLoader {

  val NECESSARY_JAVA_CLASSES: Set[String] = Set(
    "java.io.Serializable",
    "java.lang.Cloneable",

    "java.lang.Class",
    "java.lang.Object",
    "java.lang.Void",

    "java.lang.Throwable",
    "java.lang.Error",
    "java.lang.Exception",
    "java.lang.RuntimeException",

    "java.lang.String",
    "java.lang.StringBuffer",
    "java.lang.StringBuilder",
    "java.lang.Character",

    "java.lang.Boolean",

    "java.lang.Number",
    "java.lang.Byte",
    "java.lang.Short",
    "java.lang.Integer",
    "java.lang.Long",
    "java.lang.Float",
    "java.lang.Double",

    "java.lang.Math"
  )

  override def loadClass(name: String, resolve: Boolean): Class[_] = {
    if (NECESSARY_JAVA_CLASSES.contains(name) ||
        allowedClasses.contains(name)) {
      super.loadClass(name, resolve)
    }
    else {
      throw new SandboxException("Loading of class '"+name+"' not allowed with this SandboxedClassLoader.")
    }
  }

  override def getResource(name: String): URL =  {
    throw new SandboxException("Resource loading (of resource '"+name+"') not allowed with this SandboxedClassLoader.")
  }

  override def getResources(name: String): java.util.Enumeration[URL] = {
    throw new SandboxException("Resource loading (of resource '"+name+"') not allowed with this SandboxedClassLoader.")
  }

  override def getResourceAsStream(name: String): InputStream =  {
    throw new SandboxException("Resource loading (of resource '"+name+"') not allowed with this SandboxedClassLoader.")
  }

}