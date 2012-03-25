package org.skycastle.parser.model

import defs.Def
import module.Module

/**
 *
 */
case class Package(name: Symbol, parentPackage: Package = null) extends PackageContent {

  private var _contents: Map[Symbol, PackageContent] = Map()

  def add(content: PackageContent) {
    if (_contents.contains(content.name)) {
      throw new IllegalArgumentException("Package '"+name.name+"' already contains an element with the name '"+content.name.name+"'")
    }

    _contents += content.name -> content
  }
    

  def contents: Map[Symbol, PackageContent] = _contents

}