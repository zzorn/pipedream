package org.skycastle.util

import scala.collection.JavaConversions._
import java.util.HashMap

/**
 * Simple multiset implementation.
 */
final class MultiSet[T] {

  private val multiset = new HashMap[T, Int]()

  def isEmpty: Boolean = multiset.isEmpty

  def increase(t: T, amount: Int = 1) {
    require (amount >= 0)
    if (multiset.containsKey(t)) multiset.put(t, (apply(t) + amount))
    else multiset.put(t, amount)
  }

  def decrease(t: T, amount: Int = 1) {
    require (amount >= 0)
    if (multiset.containsKey(t)) {
      val value = apply(t)
      if (value <= amount) multiset.remove(t)
      else multiset.put(t, value - amount)
    }
  }

  def set(t: T, amount: Int) {
    require (amount >= 0)
    if (amount == 0) multiset.remove(t)
    else multiset.put(t, amount)
  }

  def contains(t: T): Boolean = multiset.containsKey(t)

  def apply(t: T): Int = {
    if (multiset.containsKey(t)) multiset.get(t)
    else 0
  }

  def firstKey: Option[T] = {
    if (multiset.isEmpty) None
    else Some(multiset.keys.head)
  }

  def keySet: Set[T] = multiset.keySet.toSet

  def map: Map[T, Int] = multiset.toMap
}