package org.skycastle.parser

import model.module.Module
import model.ModulePackage
import org.skycastle.utils.StringUtils
import java.io.{FileFilter, File, FilenameFilter}


/**
 * Loads modules from a filesystem path.
 */
class ModuleLoader {

  val parser: ModuleParser = new ModuleParser(new BeanFactory)

  val fileExtension = ".funlang"

  val sourceFileFilter = new FileFilter {
    def accept(file: File) = {
      file.exists() &&
        file.isFile &&
        !file.isHidden &&
        file.getName.endsWith(fileExtension) &&
        StringUtils.isIdentifier(StringUtils.removeSuffix(file.getName, fileExtension))
    }
  }

  val sourcePackageFilter = new FileFilter {
    def accept(file: File) = {
      file.exists() &&
        file.isDirectory &&
        !file.isHidden &&
        StringUtils.isIdentifier(file.getName)
    }
  }

  def loadRootModule(path: File): Module = {
    loadModule('root, path)
  }

  private def loadModule(name: Symbol, path: File): Module = {

    if (!path.exists()) throw new IllegalStateException("The specified path " + path.getPath + " doesn't exists.")

    val module = new Module(name, Nil, Nil)

    // Load modules in current directory
    val modules = path.listFiles(sourceFileFilter)
    modules foreach { f =>
      module.addDefinition(parser.parseFile(f))
    }

    // Load modules from subdirectories.
    val subPackages = path.listFiles(sourcePackageFilter)
    subPackages foreach { f =>
      module.addDefinition(loadModule(Symbol(f.getName), f))
    }

    module
  }

  private def resolveReferences() {

  }

}
