package org.skycastle.utils

import org.scalastuff.scalabeans.BeanDescriptor
import org.scalastuff.scalabeans.Preamble._

/**
 * Retrieves and caches bean descriptors.
 */
object DescriptorCache {

  private var cache: Map[Class[_], BeanDescriptor] = Map()

  def apply(clazz: Class[_]): BeanDescriptor = {
    if (cache.contains(clazz)) {
      cache(clazz)
    }
    else {
      val descriptor = descriptorOf(clazz)
      cache += clazz -> descriptor
      descriptor
    }
  }

}