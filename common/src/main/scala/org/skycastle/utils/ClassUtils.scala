package org.skycastle.utils

/**
 * Class related utilities.
 */
object ClassUtils {

  // TODO: isAssignableFrom isn't the right one to use here..

  def tToDouble[T](v: T, c: Class[T], otherHandler: (T) => Double = {(t: T) => 0.0}): Double = {
    if (c.isAssignableFrom(classOf[Byte])) v.asInstanceOf[Byte].doubleValue
    else if (c.isAssignableFrom(classOf[Short])) v.asInstanceOf[Short].doubleValue
    else if (c.isAssignableFrom(classOf[Int])) v.asInstanceOf[Int].doubleValue
    else if (c.isAssignableFrom(classOf[Long])) v.asInstanceOf[Long].doubleValue
    else if (c.isAssignableFrom(classOf[Float])) v.asInstanceOf[Float].doubleValue
    else if (c.isAssignableFrom(classOf[Double])) v.asInstanceOf[Double].doubleValue
    else otherHandler(v)
  }

  def doubleToT[T](v: Double, c: Class[T], otherHandler: (Double) => T = {d: Double => null.asInstanceOf[T]}): T = {
    if (c.isAssignableFrom(classOf[Byte])) v.byteValue.asInstanceOf[T]
    else if (c.isAssignableFrom(classOf[Short])) v.shortValue.asInstanceOf[T]
    else if (c.isAssignableFrom(classOf[Int])) v.intValue.asInstanceOf[T]
    else if (c.isAssignableFrom(classOf[Long])) v.longValue.asInstanceOf[T]
    else if (c.isAssignableFrom(classOf[Float])) v.floatValue.asInstanceOf[T]
    else if (c.isAssignableFrom(classOf[Double])) v.doubleValue.asInstanceOf[T]
    else otherHandler(v)
  }

  def nativeTypeToWrappedType(kind: Class[_]): Class[_] = {
    // TODO: Is there some way to do this in the scala libraries?  Is this missing something?  E.g. arrays?
    if (kind == classOf[Int]) classOf[java.lang.Integer]
    else if (kind == classOf[Byte]) classOf[java.lang.Byte]
    else if (kind == classOf[Short]) classOf[java.lang.Short]
    else if (kind == classOf[Long]) classOf[java.lang.Long]
    else if (kind == classOf[Float]) classOf[java.lang.Float]
    else if (kind == classOf[Double]) classOf[java.lang.Double]
    else if (kind == classOf[Boolean]) classOf[java.lang.Boolean]
    else kind
  }
}