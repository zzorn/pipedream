package org.skycastle.client.terrain.definition

import com.jme3.math.ColorRGBA

/**
 * 2D function, for terrain etc.
 */
trait Fun2d {

  def apply(x: Double, z: Double): Double = apply(x, z, 0, Double.PositiveInfinity)
  def apply(x: Double, z: Double, minScale: Double): Double = apply(x, z, minScale, Double.PositiveInfinity)
  def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double

  def +(other: Fun2d): Fun2d = AddFun(this, other)
  def -(other: Fun2d): Fun2d = SubFun(this, other)
  def *(other: Fun2d): Fun2d = MulFun(this, other)
  def /(other: Fun2d): Fun2d = DivFun(this, other)
  def max(other: Fun2d): Fun2d = MaxFun(this, other)
  def min(other: Fun2d): Fun2d = MinFun(this, other)
  def pow(other: Fun2d): Fun2d = PowFun(this, other)
  def pow(exponent: Double): Fun2d = PowToFun(this, exponent)
  def abs: Fun2d = AbsFun(this)
  def log: Fun2d = LogFun(this)
  def sin: Fun2d = SinFun(this)
  def cos: Fun2d = CosFun(this)
  def tan: Fun2d = TanFun(this)
  def clampBot(minValue: Double = 0): Fun2d = ClampBotFun(this, minValue)
  def clampTop(maxValue: Double = 0): Fun2d = ClampTopFun(this, maxValue)
  def clamp(minValue: Double = 0, maxValue: Double = 1): Fun2d = ClampFun(this, maxValue)

  // TODO: Sigmoid
  // TODO: Warp, sample other function at offset specified by third

}

/**
 * Color function, for terrain etc.
 */
trait FunColor {

  def apply(x: Double, z: Double): ColorRGBA = apply(x, z, 0, Double.PositiveInfinity)
  def apply(x: Double, z: Double, minScale: Double): ColorRGBA = apply(x, z, minScale, Double.PositiveInfinity)
  def apply(x: Double, z: Double, minScale: Double, maxScale: Double): ColorRGBA

}


final case class AddFun(f1: Fun2d, f2: Fun2d) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double =  {
    f1(x, z, minScale, maxScale) + f2(x, z, minScale, maxScale)
  }
}

final case class SubFun(f1: Fun2d, f2: Fun2d) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double =  {
    f1(x, z, minScale, maxScale) - f2(x, z, minScale, maxScale)
  }
}

final case class MulFun(f1: Fun2d, f2: Fun2d) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double =  {
    f1(x, z, minScale, maxScale) * f2(x, z, minScale, maxScale)
  }
}

final case class DivFun(f1: Fun2d, f2: Fun2d) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double =  {
    f1(x, z, minScale, maxScale) / f2(x, z, minScale, maxScale)
  }
}

final case class PowFun(f1: Fun2d, f2: Fun2d) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double =  {
    math.pow(f1(x, z, minScale, maxScale), f2(x, z, minScale, maxScale))
  }
}

final case class PowToFun(f1: Fun2d, exponent: Double = 2.0) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double =  {
    math.pow(f1(x, z, minScale, maxScale), exponent)
  }
}

final case class MaxFun(f1: Fun2d, f2: Fun2d) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double =  {
    math.max(f1(x, z, minScale, maxScale), f2(x, z, minScale, maxScale))
  }
}

final case class MinFun(f1: Fun2d, f2: Fun2d) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double =  {
    math.min(f1(x, z, minScale, maxScale), f2(x, z, minScale, maxScale))
  }
}

final case class AbsFun(f: Fun2d) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double =  {
    math.abs(f(x, z, minScale, maxScale))
  }
}

final case class LogFun(f: Fun2d) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double =  {
    math.log(f(x, z, minScale, maxScale))
  }
}

final case class SinFun(f: Fun2d) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double =  {
    math.sin(f(x, z, minScale, maxScale))
  }
}

final case class CosFun(f: Fun2d) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double =  {
    math.cos(f(x, z, minScale, maxScale))
  }
}

final case class TanFun(f: Fun2d) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double =  {
    math.tan(f(x, z, minScale, maxScale))
  }
}

final case class ClampBotFun(f: Fun2d, minValue: Double = 0.0) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double =  {
    math.max(f(x, z, minScale, maxScale), minValue)
  }
}

final case class ClampTopFun(f: Fun2d, maxValue: Double = 0.0) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double =  {
    math.min(f(x, z, minScale, maxScale), maxValue)
  }
}

final case class ClampFun(f: Fun2d, minValue: Double = 0.0, maxValue: Double = 1.0) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double =  {
    math.max(minValue, math.min(maxValue, f(x, z, minScale, maxScale)))
  }
}

final case class ConstFun(value: Double = 0.0) extends Fun2d {
  override def apply(x: Double, z: Double, minScale: Double, maxScale: Double): Double = value
}

