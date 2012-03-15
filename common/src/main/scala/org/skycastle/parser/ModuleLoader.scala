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

  def loadRootPackage(path: File): ModulePackage = {
    loadPackage(null, 'root, path)
  }

  private def loadPackage(parent: ModulePackage, name: Symbol, path: File): ModulePackage = {

    if (!path.exists()) throw new IllegalStateException("The specified path " + path.getPath + " doesn't exists.")

    val pack = new ModulePackage(name, parent)

    // Load modules in current directory
    val modules = path.listFiles(sourceFileFilter)
    modules foreach { f =>
      pack.addMember(loadModule(f))
    }

    // Load modules from subdirectories.
    val subPackages = path.listFiles(sourcePackageFilter)
    subPackages foreach { f =>
      pack.addMember(loadPackage(pack, Symbol(f.getName), f))
    }

    pack
  }

  private def loadModule(file: File): Module = {
    parser.parseFile(file)
  }

}
