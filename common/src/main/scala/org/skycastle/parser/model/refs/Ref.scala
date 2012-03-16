package org.skycastle.parser.model.refs

import org.skycastle.parser.model.expressions.Expr
import org.skycastle.parser.model.defs.Def
import org.skycastle.parser.model.PathRef

/**
 *
 */

case class Ref(path: PathRef) extends Expr {

  var definition: Def = null

  def output(s: StringBuilder, indent: Int) {
    s.append(path)
  }

  def resultType = null

  def calculation = null

  override def subNodes = singleIt(resultType)

}
