package org.skycastle.client.terrain

import com.jme3.scene.VertexBuffer.Type
import com.jme3.util.BufferUtils
import com.jme3.material.Material
import com.jme3.math.{ColorRGBA, Vector2f, Vector3f}
import com.jme3.asset.AssetManager
import java.util.{Arrays, ArrayList}
import javax.vecmath.Vector3d
import com.jme3.scene.{Node, Spatial, Geometry, Mesh}

/**
 *
 */
class TerrainBlock(
      val blockPos: BlockPos,
      terrainMaterial: Material,
      heightFunction: TerrainFunction,
      xSize: Int,
      zSize: Int,
      xExtent: Double,
      zExtent: Double,
      xOffset: Double = 0,
      zOffset: Double = 0) extends Node {

  def freeResources() {

  }


  // TODO: make it extend node.

  private var block: Geometry = null;

  lazy val blockCenter: Vector3d = calculateCenter

  def calculateCenter: Vector3d = {
    blockPos.calculateCenterPos(xExtent, heightFunction)
    /*
    val x = xOffset + xExtent * xSize / 2.0
    val z = zOffset + zExtent * zSize / 2.0
    val y = heightFunction.getHeight(x, z)
    new Vector3d(x, y, z)
    */
  }

  def getGeometry(assetManager: AssetManager): Geometry = {
    if (block == null) block = createBlock(assetManager)
    block
  }
  
  private def createBlock(assetManager: AssetManager): Geometry = {

    // 3D Mesh
    val mesh = new Mesh()


    // Data arrays
    val vertexCount: Int = xSize * zSize
    val vertices = new Array[Vector3f](vertexCount)
    val texCoords = new Array[Vector2f](vertexCount)

    // Cell index
    var i = 0
    
    // Cell coords
    var x = 0
    var z = 0
    
    // World coords
    var wx = xOffset
    var wz = zOffset
    val wxd = xExtent / (xSize - 1)
    val wzd = zExtent / (zSize - 1)
    
    // Texture coords
    var tu = 0f
    var tv = 0f
    val tud = 1f / xSize
    val tvd = 1f / zSize
    
    while (z < zSize) {
      x = 0
      wx = xOffset.toFloat
      tu = 0f
      while (x < xSize) {

        // Point location
        val wy = heightFunction.getHeight(wx, wz)
        vertices(i) = new Vector3f(wx.toFloat, wy.toFloat, wz.toFloat)

        // Texture location
        texCoords(i) = new Vector2f(tu, tv)


        i += 1
        x += 1
        wx += wxd
        tu += tud
      }
      z += 1
      wz += wzd
      tv += tvd
    }
    
    
    // Triangles connecting points
    /*
      Triangles should be counter-clockwise

      Vertex buffer:
      0---1---2
      | / | / |
      3---4---5
      | \ | \ |
      6---7---8

      Triangle strip indexes:

        First triangle:
          0,3,1,
        Additional tris, using previous two points as first points
          4,
          2,
          5,
        Switch row
          5,
        Back the other way
          8,
          4,
          7,
          3,
          6
     */
    val xCellCount: Int = xSize - 1
    val zCellCount: Int = zSize - 1
    val indexCount = 2 + xCellCount * zCellCount * 2 + (zCellCount - 1) * 3
    val indexes = new Array[Int](indexCount)
    mesh.setMode(Mesh.Mode.TriangleStrip)
    //val indexes = Array[Int](0,3,1,4,2,5,5,5,8,4,7,3,6)
    var triangleIndex = 0
    var xCell = 0
    var zCell = 0

    def addIndex(vertex: Int) {
      indexes(triangleIndex) = vertex
      triangleIndex += 1
    }

    // Add initial indexes
    addIndex(0)
    addIndex(xSize)

    while (zCell < zCellCount) {
      xCell = 0
      while (xCell < xCellCount) {

        // From left to right on even rows, right to left on odd rows
        val vertexIndex = if (zCell % 2 == 0) xSize * zCell + xCell + 1 else xSize * zCell + xSize - xCell - 2

        addIndex(vertexIndex)
        addIndex(vertexIndex + xSize)

        xCell += 1
      }

      zCell += 1

      if (zCell < zCellCount) {
        // Add some empty degenerate triangles to tie together consecutive rows
        val vertexIndex = if (zCell % 2 == 0) zCell * xSize else zCell * xSize + xSize - 1

        addIndex(vertexIndex)
        addIndex(vertexIndex)
        addIndex(vertexIndex + xSize)
      }
    }


    assert(indexCount == triangleIndex, "Triangle index ("+triangleIndex+") should equal the calculated index count "+indexCount)


    // Store in buffer
    mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices: _*));
    mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoords: _*));
    mesh.setBuffer(Type.Index,    1, BufferUtils.createIntBuffer(indexes: _*));
    mesh.updateBound();

    mesh.setStatic()

    // Create geometry
    val geo = new Geometry("OurMesh", mesh);
    //val mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    //mat.setColor("Color", ColorRGBA.Green);
    //mat.setColor("Color", ColorRGBA.Blue);

    // Wireframe mode
    //mat.getAdditionalRenderState.setWireframe(true);

    geo.setMaterial(terrainMaterial);

    geo
  }


}