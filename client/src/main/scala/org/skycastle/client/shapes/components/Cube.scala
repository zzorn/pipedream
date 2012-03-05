package org.skycastle.client.shapes.components

import com.jme3.scene.Mesh
import org.skycastle.utils.MeshBuilder
import com.jme3.math.Vector3f

/**
 *
 */

case class Cube(var sizeX: Float = 1f,
                var sizeY: Float = 1f,
                var sizeZ: Float = 1f) extends Model {

  def createMesh(): Mesh  = {

    val builder = new MeshBuilder()

    val v000 = builder.addVertex(new Vector3f(-sizeX, -sizeY, -sizeZ))
    val v001 = builder.addVertex(new Vector3f(-sizeX, -sizeY,  sizeZ))
    val v010 = builder.addVertex(new Vector3f(-sizeX,  sizeY, -sizeZ))
    val v011 = builder.addVertex(new Vector3f(-sizeX,  sizeY,  sizeZ))
    val v100 = builder.addVertex(new Vector3f( sizeX, -sizeY, -sizeZ))
    val v101 = builder.addVertex(new Vector3f( sizeX, -sizeY,  sizeZ))
    val v110 = builder.addVertex(new Vector3f( sizeX,  sizeY, -sizeZ))
    val v111 = builder.addVertex(new Vector3f( sizeX,  sizeY,  sizeZ))

    builder.addQuad(v000, v001, v011, v010)
    builder.addQuad(v100, v110, v111, v101)

    builder.addQuad(v010, v011, v111, v110)
    builder.addQuad(v000, v100, v101, v001)

    builder.addQuad(v001, v101, v111, v011)
    builder.addQuad(v000, v010, v110, v100)

    builder.createMesh()
  }

}
