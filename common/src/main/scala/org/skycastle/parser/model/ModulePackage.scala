package org.skycastle.parser.model

import org.skycastle.parser.model.Referable

/**
 * A package that contains Modules.
 */
case class ModulePackage(name: Symbol, parent: ModulePackage) extends Referable with Outputable {

  private var members: Map[Symbol, Referable with Outputable] = Map()
  
  def addMember(member: Referable with Outputable) {
    members += member.name -> member
  }

  def getMember(name: Symbol): Option[Referable with Outputable] = {
    members.get(name)
  }

  def output(s: StringBuilder, indent: Int) {
    createIndent(s, indent)
    s.append("package ").append(name.name).append(" { \n\n")

    outputSeparatedList(members.values, s, indent + 1, "\n\n")

    createIndent(s, indent)
    s.append("}\n")
  }
}
