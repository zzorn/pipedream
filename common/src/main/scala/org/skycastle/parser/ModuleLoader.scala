package org.skycastle.parser

import model.defs.{ValDef, Parameter, FunDef, Def}
import model.module.{Import, Module}
import model.refs.{Call, Ref}
import model.SyntaxNode
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
    val root = loadModule('root, path)
    val errors = resolveReferences(root)
    if (!errors.isEmpty) throw errors.head
    root
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


  private def resolveReferences(root: Module):List[ParseError] = {
    var errors: List[ParseError] = Nil

    def addError(msg: String) {
      // TODO: Add location and file data to parsed syntax nodes!
      errors ::= new ParseError(msg, null, null)
    }

    // Imports
    root.visitClasses(classOf[Import]) { (context, imp) =>
      root.getMemberByPath(imp.path) match {
        case Some(definition) => imp.importedDef = definition
        case None => addError("Could not resolve imported definition "+imp.path+" ")
      }
    }

    // References
    root.visitClasses(classOf[Ref]) { (context, ref) =>
      SyntaxNode.getReferencedDefinition(context, ref.path.path) match {
        case Some(definition) => ref.definition = definition
        case None => addError("Could not resolve reference "+ref.path+" ")
      }
    }

    // Types
    // TODO

    // Function calls
    root.visitClasses(classOf[Call]) { (context, call) =>
      SyntaxNode.getReferencedDefinition(context, call.functionRef.path) match {
        case Some(definition: FunDef)    => call.functionDef = definition
        case Some(definition: Parameter) => call.functionDef = definition
        case Some(definition: ValDef)    => call.functionDef = definition
        case Some(otherDef) => addError("Can not call "+call.functionRef+" ")
        case None => addError("Could not resolve called function reference "+call.functionRef+" ")
      }
    }

    // Parameters, and Named arguments
    // TODO

    // TODO: Support builtin modules and types that are available to the loaded code

    errors
  }

}
