package org.skycastle.parser.model.refs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.defs.Def
import org.skycastle.parser.model.PathRef

/**
 *
 */

case class Ref(definitionRef: PathRef) extends Expr {

  var definition: Def = null

  def output(s: StringBuilder, indent: Int) {
    if (definition == null) {
      s.append(definitionRef)
    }
    else {
      s.append(definition.name)
    }
  }

  def resultType = null

  def calculation = null
}
