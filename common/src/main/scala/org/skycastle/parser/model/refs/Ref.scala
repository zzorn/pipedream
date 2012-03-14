package org.skycastle.parser.model.refs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.defs.Def

/**
 *
 */

case class Ref(definitionName: Symbol) extends Expr {

  var definition: Def = null

  def output(s: StringBuilder, indent: Int) {
    if (definition == null) {
      s.append(definitionName.name)
    }
    else {
      s.append(definition.name)
    }
  }

  def resultType = null

  def calculation = null
}
