package org.skycastle.utils

/**
 *
 */

object StringUtils {

  /**
   * Adds a prefix, capitalizing the body, except if the prefix if empty, in which case the body is returned as-is.
   */
  def addPrefix( prefix : String, body : String ) : String = {
    if (prefix == null || prefix == "") body
    else prefix + capitalize( body )
  }

  def removeSuffix(s: String, suffix: String): String = {
    if (s.endsWith(suffix)) {
      s.substring(0, s.length() - suffix.length())
    }
    else s
  }

  /**
   * Changes the first letter to upper case of the string.
   */
  def capitalize( s : String ) : String = {
    if (s == null || s.isEmpty) s
    else s(0).toUpperCase.toString + s.substring(1)
  }

  /**
   *  Adds zeroes in front of a non-negative integer until we get a string of the desired length.
   */
  def zeroPadInteger(number: Int, desiredLength: Int) : String = {
    if (number < 0) throw new IllegalArgumentException("Argument should not be negative")

    var numZeropad = number.toString
    while (numZeropad.length < desiredLength) {
      numZeropad = "0" + numZeropad;
    }
    return numZeropad
  }

  /**
   * True if s is a java style identifier (starts with letter, followed by numbers and letters, should be non-empty).
   */
  def isIdentifier( s : String ) : Boolean = {
    if (s == null || s.length <= 0) false
    else if ( !Character.isJavaIdentifierStart( s.charAt( 0 ) ) ) false
    else {
      s forall Character.isJavaIdentifierPart
    }
  }

}