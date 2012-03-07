package org.skycastle.client.shapes.components
import reflect.BeanProperty
import org.skycastle.functions.Noise1to3
import com.jme3.scene.Mesh
import org.skycastle.utils.MeshBuilder
import com.jme3.math.{Matrix4f, Transform}


/**
 *
 */

case class ScatterAlong(@BeanProperty var model: Model,
                   @BeanProperty var start: Double = 0.0,
                   @BeanProperty var end: Double = 1.0,
                   @BeanProperty var number: Int = 10,
                   @BeanProperty var path: Double => (Double, Double, Double) = t => (0.0, 0.0, 0.0),
                   @BeanProperty var scale: Double => Double = t => 1.0) extends Model {

  def this() {
    this(null)
  }


  def buildMesh(builder: MeshBuilder) {
    if (model != null && number > 0) {
      val localTransform = new Matrix4f()

      var t = start
      val step = (end - start) / number
      while (t <= end) {
        val pos = path(t)
        val s = scale(t).toFloat

        // Transform according to functions
        localTransform.loadIdentity()
        localTransform.setTranslation(pos._1.toFloat, pos._2.toFloat, pos._3.toFloat)
        localTransform.setScale(s, s, s)

        model.buildMesh(builder, localTransform)

        t += step
      }
    }
  }
  
}
