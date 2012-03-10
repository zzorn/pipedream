package org.skycastle.parser.model

/**
 *
 */
final case class Module(imports: List[Import], functions: List[Definition]) extends Outputable {

  def output(s: StringBuilder, indent: Int){
    s.append("\n")

    outputSeparatedList(imports, s, indent + 1, "")
    s.append("\n")

    outputSeparatedList(functions, s, indent + 1, "\n")
    s.append("\n")
  }
}