package org.skycastle.client.terrain

import com.jme3.scene.VertexBuffer.Type
import com.jme3.util.BufferUtils
import com.jme3.material.Material
import com.jme3.math.{ColorRGBA, Vector2f, Vector3f}
import com.jme3.scene.{Spatial, Geometry, Mesh}
import com.jme3.asset.AssetManager

/**
 *
 */
class TerrainBlock {

  def createBlock(assetManager: AssetManager): Spatial = {

    // 3D Mesh
    val mesh = new Mesh()

    // Point locations
    val vertices = new Array[Vector3f](4)
    vertices(0) = new Vector3f(0,0,0)
    vertices(1) = new Vector3f(3,0,0)
    vertices(2) = new Vector3f(0,3,0)
    vertices(3) = new Vector3f(3,3,0)

    // Texture locations at points
    val texCoord = new Array[Vector2f](4)
    texCoord(0) = new Vector2f(0,0)
    texCoord(1) = new Vector2f(1,0)
    texCoord(2) = new Vector2f(0,1)
    texCoord(3) = new Vector2f(1,1)

    // Triangles connecting points
    /*
      2--3
      |\ | Counter-clockwise
      | \|
      0--1
     */
    val indexes = Array[Int]( 2,0,1, 1,3,2 )


    // Store in buffer
    mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices: _*));
    mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord: _*));
    mesh.setBuffer(Type.Index,    1, BufferUtils.createIntBuffer(indexes: _*));
    mesh.updateBound();

    // Create geometry
    val geo = new Geometry("OurMesh", mesh);
    val mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", ColorRGBA.Blue);
    geo.setMaterial(mat);

    geo
  }


}