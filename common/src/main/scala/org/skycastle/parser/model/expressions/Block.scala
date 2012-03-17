package org.skycastle.parser.model.expressions

import org.skycastle.parser.model.defs.{Parameter, Def}
import org.skycastle.parser.ResolverContext
import org.skycastle.parser.model.{SyntaxNode, TypeDef, Context}


/**
 *
 */
final case class Block(definitions: List[Def], value: Expr) extends Expr {

  private val definitionsByName: Map[Symbol, Def] = definitions.map(d => d.name -> d).toMap

  def output(s: StringBuilder, indent: Int) {
    //s.append("\n")
    //createIndent(s, indent - 1)
    s.append("{")
    s.append("\n")

    if (!definitions.isEmpty) {
      outputTerminatedList(definitions, s, indent, "\n")
    }

    createIndent(s, indent)
    s.append("return ")
    value.output(s, indent)
    s.append("\n")

    createIndent(s, indent - 1)
    s.append("}")
//    s.append("\n")
  }

  override def hasContext = true
  override def getContextNamedDef(name: Symbol): Option[Def] = definitionsByName.get(name)

  override def subNodes = definitions.iterator ++ singleIt(value) ++ singleIt(valueType)

  def determineValueType(visitedNodes: Set[SyntaxNode]): TypeDef = value.valueType(visitedNodes)
}