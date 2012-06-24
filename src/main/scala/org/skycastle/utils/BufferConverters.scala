package org.skycastle.utils

import java.util.ArrayList
import com.jme3.util.BufferUtils
import java.nio.{FloatBuffer, IntBuffer}
import com.jme3.math.{ColorRGBA, Vector3f, Vector2f}

/**
 *
 */

object BufferConverters {


  def createIntBuffer(data: ArrayList[Int]): IntBuffer = {

    val buff = BufferUtils.createIntBuffer(data.size())

    var i = 0
    while (i < data.size()) {
      buff.put(data.get(i))
      i += 1
    }

    buff.flip

    buff
  }

  def createVector2fBuffer(data: ArrayList[Vector2f]): FloatBuffer = {

    val buff: FloatBuffer = BufferUtils.createFloatBuffer(2 * data.size())

    var i = 0
    while (i < data.size()) {
      val v = data.get(i)
      if (v != null) {
        buff.put(v.x)
        buff.put(v.y)
      } else {
        buff.put(0f)
        buff.put(0f)
      }

      i += 1
    }

    buff.flip

    buff
  }

  def createVector3fBuffer(data: ArrayList[Vector3f]): FloatBuffer = {

    val buff: FloatBuffer = BufferUtils.createFloatBuffer(3 * data.size())

    var i = 0
    while (i < data.size()) {
      val v = data.get(i)
      if (v != null) {
        buff.put(v.x)
        buff.put(v.y)
        buff.put(v.z)
      } else {
        buff.put(0f)
        buff.put(0f)
        buff.put(0f)
      }

      i += 1
    }

    buff.flip

    buff
  }

  def createColorRGBABuffer(data: ArrayList[ColorRGBA]): FloatBuffer = {

    val buff: FloatBuffer = BufferUtils.createFloatBuffer(4 * data.size())

    var i = 0
    while (i < data.size()) {
      val v = data.get(i)
      if (v != null) {
        buff.put(v.r)
        buff.put(v.g)
        buff.put(v.b)
        buff.put(v.a)
      } else {
        buff.put(0f)
        buff.put(0f)
        buff.put(0f)
        buff.put(0f)
      }

      i += 1
    }

    buff.flip

    buff
  }

}
