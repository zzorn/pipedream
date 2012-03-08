package org.skycastle.parser.serializers

import java.awt.Color
import java.util.Date

/**
 * Contains some serializers and deserializers for commonly used value classes.
 */
class StandardSerializers extends SerializersImpl {

  def esc(v: String): String = "\"" + v + "\""

  registerSerializer[java.lang.Boolean](v => v.toString, s => new java.lang.Boolean(s))
  registerSerializer[java.lang.Byte]( v => v.toString, s => new java.lang.Byte(s))
  registerSerializer[java.lang.Short]( v => v.toString, s => new java.lang.Short(s))
  registerSerializer[java.lang.Integer]( v => v.toString, s => new java.lang.Integer(s))
  registerSerializer[java.lang.Long]( v => v.toString, s => new java.lang.Long(s))
  registerSerializer[java.lang.Float]( v => v.toString, s => new java.lang.Float(s))
  registerSerializer[java.lang.Double]( v => v.toString, s => new java.lang.Double(s))
  registerSerializer[java.lang.Character]( v => esc(v.toString), s => s.charAt(0))
  registerSerializer[String]( v => esc(v), s => s)
  registerSerializer[Color]( (v: Color) => esc(Integer.toHexString(v.getRGB())), s => new Color(Integer.decode(s).intValue) )
  registerSerializer[Symbol]( (v: Symbol) => esc(v.name), s => Symbol(s) )


}
