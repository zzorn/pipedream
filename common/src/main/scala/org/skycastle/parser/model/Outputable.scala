package org.skycastle.parser.model


/**
 *
 */
trait Outputable {

  def output(s: StringBuilder, indent: Int)

  override def toString: String = {
    val s = new StringBuilder
    output(s, 0)
    s.toString()
  }

  protected def indentString(i: Int): String = {
    val s  = new StringBuilder()
    createIndent(s, i)
    s.toString()
  }

  protected def createIndent(s: StringBuilder, i: Int) {
    var n = 0
    while (n < i) {
      s.append("  ")
      n += 1
    }
  }

  protected def outputSeparatedList(list: List[_ <: Outputable], s: StringBuilder, indent: Int, separator: String = ", ") {
    var i = 0
    list foreach {l =>
      l.output(s, indent)
      if (i < list.size - 1) s.append(separator)
      i += 1
    }
  }

}