package org.skycastle.utils

import util.parsing.combinator.syntactical.StandardTokenParsers
import org.skycastle.parser.model.module.Module
import org.skycastle.parser.{ParseError, BeanFactory}
import util.parsing.input.{PagedSeqReader, CharSequenceReader}
import collection.immutable.PagedSeq
import java.io._
import util.parsing.combinator.PackratParsers

/**
 * Easy to use parser base class for custom language parsers.
 * Use registerKeywords to register reserved keywords in the language, and
 * registerDelimiters to reserve special characters or character sequences used.
 */
abstract class LanguageParser[T] extends StandardTokenParsers with PackratParsers {

  override val lexical = new LanguageLexer()

  override type Elem = lexical.Token

  /**
   * Registers the specified reserved language keywords.
   */
  def registerKeywords(keywords: String *) {
    keywords foreach registerKeyword
  }

  /**
   * Registers the specified reserved language keyword, and returns it.
   */
  def registerKeyword(keyword: String): String = {
    lexical.reserved.add(keyword)
    keyword
  }

  /**
   * Registers the specified special character sequences.
   */
  def registerDelimiters(delimiters: String *) {
    delimiters foreach registerDelimiter
  }

  /**
   * Registers the specified reserved language special character sequence, and returns it.
   */
  def registerDelimiter(delimiter: String): String = {
    lexical.delimiters.add(delimiter)
    delimiter
  }



  /**
   * Override this to define the root level parser.
   */
  def rootParser: Parser[T]

  /**
   * A parser for quoted strings.
   */
  lazy val quotedString = accept("string", { case lexical.StringLit(n) => n} )

  /**
   * A parser for (double) numbers.
   */
  lazy val doubleNumber = accept("number", { case lexical.NumericLit(n) => n.toDouble} )



  /**
   * Parse input from a reader.
   * @param sourceName the name to use for the input in any error messages, or leave null for no name.
   * @param allowTrailingContent if true, will not throw an error if there is content left in the input
   *                             after the content has been parsed (e.g. a file could contain many inputs after each other).
   *                             Defaults to false.
   */
  def parse(input: java.io.Reader, sourceName: String = null, allowTrailingContent: Boolean = false): T = {
    val scanner = new lexical.Scanner(new PagedSeqReader(PagedSeq.fromReader(input)))
    parseTokens(scanner, sourceName, allowTrailingContent)
  }

  /**
   * Parse a character sequence, e.g. a String.
   * @param sourceName the name to use for the input in any error messages, or leave null for no name.
   * @param allowTrailingContent if true, will not throw an error if there is content left in the input
   *                             after the content has been parsed (e.g. a file could contain many inputs after each other).
   *                             Defaults to false.
   */
  def parseString(input: java.lang.CharSequence, sourceName: String = null, allowTrailingContent: Boolean = false): T = {
    val scanner = new lexical.Scanner(new CharSequenceReader(input))
    parseTokens(scanner, sourceName, allowTrailingContent)
  }

  /**
   * Parse input from a stream.
   * @param sourceName the name to use for the input in any error messages, or leave null for no name.
   * @param allowTrailingContent if true, will not throw an error if there is content left in the input
   *                             after the content has been parsed (e.g. a file could contain many inputs after each other).
   *                             Defaults to false.
   */
  def parseStream(input: InputStream, sourceName: String = null, allowTrailingContent: Boolean = false): T = {
    parse(new InputStreamReader(input), sourceName, allowTrailingContent)
  }

  /**
   * Parse input from a file.  May throw IO exceptions if the file could not be read.
   * @param sourceName the name to use for the input in any error messages, or leave null to use the filename.
   * @param allowTrailingContent if true, will not throw an error if there is content left in the input
   *                             after the content has been parsed (e.g. a file could contain many inputs after each other).
   *                             Defaults to false.
   */
  def parseFile(input: File, sourceName: String = null, allowTrailingContent: Boolean = false): T = {
    val name = if (sourceName == null) input.getPath else sourceName

    var reader: Reader = null
    try {
      reader = new BufferedReader(new FileReader(input))
      parse(reader, name, allowTrailingContent)
    }
    finally {
      if (reader != null) reader.close()
    }
  }


  private def parseTokens(tokenScanner: lexical.Scanner, sourceName: String, allowTrailingContent: Boolean): T = {
    val parser = if (allowTrailingContent) rootParser else phrase(rootParser)
    parser(tokenScanner) match {
      case s: Success[T] =>  s.result
      case f: NoSuccess => throw new ParseError(f.msg, f.next.pos, sourceName)
    }
  }



}
