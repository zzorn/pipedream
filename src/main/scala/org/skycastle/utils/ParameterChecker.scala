package org.skycastle.utils


/**
 * Provides utility functions for checking prameter preconditions.
 *
 * Import e.g. with ParameterChecker._ to minimize syntactic noise.
 *
 * Throws IllegalArgumentException with a user readable message if the requirement fails.
 *
 * @author Hans Haggstrom
 */
object ParameterChecker {

  def requireNotNull(value: Any, parameterName: Symbol) {
    test(value != null, value, parameterName, "not be null")
  }

  def requireNotEmpty(value: Iterable[_], parameterName: Symbol) {
    requireNotNull(value, parameterName)

    test(!value.isEmpty, value, parameterName, "not be empty")
  }

  def requirePositiveInteger(value: Int, parameterName: Symbol) {
    test(value > 0, value, parameterName, "be larger than 0")
  }

  def requirePositive(value: Double, parameterName: Symbol) {
    test(value > 0, value, parameterName, "be larger than 0")
  }

  def requireInRange(value: Int, parameterName: Symbol, minInclusive: Int, maxExclusive: Int) {
    test(value >= minInclusive && value < maxExclusive, value, parameterName, "be in the range " + minInclusive + " (inclusive) to " + maxExclusive + " (exclusive)")
  }


  /**
   * Should be a valid identifier (letter or underscore or dollar followed by the same or numbers).
   */
  def requireIsIdentifier(s: String, parameterName: Symbol) {

    test(StringUtils.isIdentifier(s), s, parameterName, "be a valid identifier")
  }

  /**
   * Should be a valid identifier (letter or underscore or dollar followed by the same or numbers).
   */
  def requireIsIdentifier(symbol: Symbol, parameterName: Symbol) {
    requireNotNull(symbol, parameterName)
    test(StringUtils.isIdentifier(symbol.name), symbol, parameterName, "be a Symbol containing a valid identifier")
  }

  /**
   * Some collection should be the specified size.
   */
  def requireSizeEquals(value: Seq[_], parameterName: Symbol, expectedSize: Int) {

    requireNotNull(value, parameterName)

    test(value.size == expectedSize, "of size " + value.size + " (" + value + ")", parameterName, "be of size " + expectedSize)
  }


  private def test(testResult: Boolean, value: Any, parameter: Symbol, requirement: String) {
    if (!testResult) {
      val stringValue = if (value == null) "null" else value.toString
      throw new IllegalArgumentException("The parameter '" + parameter.name + "' should " + requirement + ", but it was '" + stringValue + "'.")
    }
  }

}


