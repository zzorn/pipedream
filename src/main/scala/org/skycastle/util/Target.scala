package org.skycastle.util

trait Target {

  def inside(value: Double): Double

}

case object AnyTarget extends Target {
  def inside(value: Double) = 1.0
}

case class Over(target: Double, acceptableVariation: Double = 0.1) extends Target {
  require(acceptableVariation >= 0, "acceptableVariation should be larger or equal to zero")

  def minAcceptable = target * (1.0 - acceptableVariation)

  def inside(value: Double): Double = {
    if (value >= target) 1
    else if (value < minAcceptable) 0
    else (value - minAcceptable) / (target - minAcceptable)
  }
}

case class Under(target: Double, acceptableVariation: Double = 0.1) extends Target {
  require(acceptableVariation >= 0, "acceptableVariation should be larger or equal to zero")

  def maxAcceptable = target * (1.0 + acceptableVariation)

  def inside(value: Double): Double = {
    if (value <= target) 1
    else if (value > maxAcceptable) 0
    else 1.0 - (value - target) / (maxAcceptable - target)
  }
}

/**
 * Represents a target and a maximum percentage variation around it.
 */
case class OnTarget(target: Double, onTargetVariation: Double = 0.1, acceptableVariation: Double = 0.2) extends Target {
  require(onTargetVariation >= 0, "onTargetVariation should be zero or larger")
  require(acceptableVariation >= 0, "acceptableVariation should be larger or equal to zero")

  def minAcceptable = target * (1.0 - onTargetVariation - acceptableVariation)
  def maxAcceptable = target * (1.0 + onTargetVariation + acceptableVariation)
  def minTarget = target * (1.0 - onTargetVariation)
  def maxTarget = target * (1.0 + onTargetVariation)

  /**
   * Returns 1 if the value is inside the acceptable range, 0 if it is outsize the maximum allowed range,
   * and a value in between depending on how far the value is between the acceptable and allowed range.
   */
  def inside(value: Double): Double = {
    if (value >= minTarget && value <= maxTarget) 1
    else if (value < minAcceptable || value > maxAcceptable) 0
    else if (value < target) (value - minAcceptable) / (minTarget - minAcceptable)
    else 1.0 - (value - (maxTarget)) / (maxAcceptable - maxTarget)
  }

}