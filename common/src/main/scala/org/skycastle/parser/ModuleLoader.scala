package org.skycastle.parser

import model._
import expressions.{FunExpr, Expr}
import model.defs.{ValDef, Parameter, FunDef, Def}
import model.module.{Import, Module}
import model.refs.{Arg, Call, Ref}
import org.skycastle.utils.StringUtils
import java.io.{FileFilter, File, FilenameFilter}
import org.skycastle.utils.LangUtils._

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

    // TODO: Support builtin modules and types that are available to the loaded code

    def addError(msg: String, location: SyntaxNode) {
      // TODO: Add location and file data to parsed syntax nodes!
      errors ::= new ParseError(msg, null, null)
    }

    // Imports
    root.visitClasses(classOf[Import]) { (context, imp) =>
      root.getMemberByPath(imp.path) match {
        case Some(definition) => imp.importedDef = definition
        case None => addError("Could not resolve imported definition "+imp.path+" ", imp)
      }
    }

    // Resolve references
    // Also checks for missing references
    root.visitClasses(classOf[Ref]) { (context, ref) =>
      context.getDef(ref.path.path) match {
        case Some(definition: ValueTyped) => ref.definition = definition
        case None => addError("Could not resolve reference "+ref.path+" ", ref)
        case x => addError("Cannot refer to a "+x.get.name.name+".", ref)
      }
    }

    // Resolve function calls
    // Also checks for missing references or invalid calls
    root.visitClasses(classOf[Call]) { (context, call) =>
      context.getDef(call.functionRef.path) match {
        case Some(definition: FunDef)    => call.functionDef = definition
        case Some(definition: Parameter) => call.functionDef = definition
        case Some(definition: ValDef)    => call.functionDef = definition
        case Some(otherDef) => addError("Can not call "+call.functionRef+" ", call)
        case None => addError("Could not resolve called function reference "+call.functionRef+" ", call)
      }
    }


    // Check for missing types and cyclic references
    root.visitClasses(classOf[ValueTyped]) { (context, exp) =>
      if (exp.valueType == null) addError("Could not determine the type of the expression '" + exp + "'", exp)
    }

    // Check for missing return types
    root.visitClasses(classOf[FunDef]) { (context, exp: ReturnTyped) =>
      if (exp.returnType == null) addError("Could not determine the return type of the function '" + exp + "'", exp)
    }
    root.visitClasses(classOf[FunExpr]) { (context, exp: ReturnTyped) =>
      if (exp.returnType == null) addError("Could not determine the return type of the function expression '" + exp + "'", exp)
    }

    // Check that function call parameter types and named parameters match the function definition they are calling
    // Check that reference is a function or func expr.
    root.visitClasses(classOf[Call]) { (context: ResolverContext, call: Call) =>
      call.functionDef match {
        case callable: Callable =>
          var providedParameters: Set[Parameter] = Set()

          def checkParamTypeAndAdd(param: Parameter, argument: Arg) {
            if (!param.valueType.isAssignableFrom(argument.value.valueType)) {
              addError("Wrong type passed to parameter '" + param.name.name + "' in function " + callable.nameAndSignature + ", " +
                       "expected " + param.valueType + " but argument was of type " + argument.value.valueType + ".", call)
            }
            if (providedParameters.contains(param)) {
              addError("Cannot specify the parameter '" + param.name.name + "' in function " + callable.nameAndSignature +
                       " with argument '"+argument+"', it is already specified by another argument.", call)
            }
            else {
              providedParameters += param
            }
          }

          var index = 0

          call.arguments foreach { argument: Arg =>
            if (argument.paramName.isEmpty) {
              // Non named argument
              callable.parameterByIndex(index) match {
                case Some(param: Parameter) =>
                  checkParamTypeAndAdd(param, argument)
                case None =>
                  addError("Too many parameters passed to function " + callable.nameAndSignature, call)
              }
              index += 1
            }
            else {
              // Named argument
              callable.parameterByName(argument.paramName.get) match {
                case Some(param: Parameter) =>
                  checkParamTypeAndAdd(param, argument)
                case None =>
                  addError("No parameter named '"+argument.paramName.get+"' found for function " + callable.nameAndSignature, call)
              }
            }
          }

          // Check that all needed parameters were provided
          callable.parameters foreach { parameter =>
            if (!providedParameters.contains(parameter) && parameter.defaultValue.isEmpty) {
              addError("The required parameter '"+parameter.name.name+"' of function " + callable.nameAndSignature + " has no argument specified.", call)
            }
          }

        case otherDef: Def with ValueTyped =>
          // E.g. parameter of value with function object that has no named parameters
          if (!otherDef.valueType.isInstanceOf[FunType]) {
            addError("Can not call non-function type value '"+otherDef.valueType+"'", call)
          }
          else {
            val funType: FunType = otherDef.valueType.asInstanceOf[FunType]

            if (call.arguments.size != funType.parameterTypes.size) {
              addError("Invalid number of arguments passed to function '"+funType+"', " +
                       "expected "+funType.parameterTypes.size+" but "+call.arguments.size+" were provided.", call)
            }
            else {
             var index = 0
              call.arguments foreach { argument: Arg =>
                if (argument.paramName.isDefined) {
                  addError("Named argument not allowed here", call)
                }
                else if (!funType.parameterTypes(index).isAssignableFrom(argument.value.valueType)) {
                  addError("Wrong type passed to parameter number " + (index+1) + " in function " + otherDef.valueType + ", " +
                    "expected " + funType.parameterTypes(index) + " but argument was of type " + argument.value.valueType + ".", call)
                }
                index += 1
              }
            }
          }

        case _ =>
          addError("Can not invoke a function call on an expression ("+call.functionDef+") of type '"+(if (call.functionDef != null) call.functionDef.valueType else "[UnknownType]")+"' ", call)
      }

    }


    // Check that value expressions are of correct types
    def checkTypes[T <: ReturnTyped](kind: Class[T], msg: String,  actualCalc: T => TypeDef) {
      root.visitClasses(kind) { (context, returnTyped: T) =>
        if (returnTyped.declaredReturnType.isDefined) {
          val expected: TypeDef = returnTyped.returnType
          val actual: TypeDef = actualCalc(returnTyped)
          if (!expected.isAssignableFrom(actual)) {
            addError(msg + " does not correcpond to declared type, expected '"+expected+"', " +
              "but got '"+actual+"'", returnTyped)
          }
        }
      }
    }
    
    checkTypes[FunDef](classOf[FunDef], "Type of function expression", ref => ref.expression.valueType)
    checkTypes[ValDef](classOf[ValDef], "Type of val expression", ref => ref.value.valueType)
    checkTypes[FunExpr](classOf[FunExpr], "Type of function expression value", ref => ref.expression.valueType)
    checkTypes[Parameter](classOf[Parameter], "Type of parameter default value", ref => ref.defaultValue.map(p => p.valueType).getOrElse(ref.returnType))



    errors.reverse
  }



}
