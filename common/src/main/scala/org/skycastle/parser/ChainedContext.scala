package org.skycastle.parser

/**
 *
 */
case class ChainedContext(primaryContext: Context, fallbackContext: Context) extends Context {
  override def parentContext = fallbackContext
  override def getNestedValue(name: Symbol) = primaryContext.getNestedValue(name)
}