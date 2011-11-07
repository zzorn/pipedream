package org.skycastle.util

import scala.collection._

/**
 * Simple multiset implementation.
 */
final class MultiSet[T] {

  private val multiset: mutable.Map[T, Int] = mutable.Map[T, Int]()

  def isEmpty: Boolean = multiset.isEmpty

  def increase(t: T, amount: Int = 1) {
    require (amount >= 0)
    if (multiset.contains(t)) multiset + (t -> (apply(t) + amount))
    else multiset + (t -> amount)
  }

  def decrease(t: T, amount: Int = 1) {
    require (amount >= 0)
    if (multiset.contains(t)) {
      val value = apply(t)
      if (value <= amount) multiset - t
      else multiset + (t -> (value - amount))
    }
  }

  def set(t: T, amount: Int) {
    require (amount >= 0)
    if (amount == 0) multiset - t
    else multiset + (t -> amount)
  }

  def contains(t: T): Boolean = multiset.contains(t)

  def apply(t: T): Int = {
    multiset.get(t) match {
      case Some(value) => value
      case None => 0
    }
  }

  def keySet: Set[T] = multiset.keySet

  def map: immutable.Map[T, Int] = multiset.toMap
}