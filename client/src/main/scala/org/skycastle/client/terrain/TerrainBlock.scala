package org.skycastle.client.terrain

import com.jme3.scene.VertexBuffer.Type
import com.jme3.util.BufferUtils
import com.jme3.material.Material
import com.jme3.asset.AssetManager
import java.util.{Arrays, ArrayList}
import javax.vecmath.Vector3d
import com.jme3.scene.{Node, Spatial, Geometry, Mesh}
import com.jme3.math.{Vector3f, ColorRGBA, Vector2f}

/**
 *
 */
class TerrainBlock(
      val blockPos: BlockPos,
      terrainMaterial: Material,
      terrainFunction: TerrainFunction,
      sizeSettings: GroundSizeSettings,
      sleeveDownPullFactor: Float = 0.1f) extends Node("TerrainBlock") {

  def freeResources() {
    detachAllChildren()

    // TODO: Anything else?
  }

  private var block: Geometry = null;

  block = createBlock()
  attachChild(block)

  def getGeometry(assetManager: AssetManager): Spatial = {
    this
  }
  
  private def createBlock(): Geometry = {

    val tempTextureScale = 1.0/256;

    // 3D Mesh
    val mesh = new Mesh()


    // Data arrays
    val cellCount: Int = sizeSettings.blockCellCount
    val vertexSize = cellCount + 1 + 2
    val totalVertexCount: Int = vertexSize * vertexSize
    val vertices = new Array[Vector3f](totalVertexCount)
    val texCoords = new Array[Vector2f](totalVertexCount)
    val normals = new Array[Vector3f](totalVertexCount)

    def wrap(c: Int): Int = (c + vertexSize) % vertexSize
    def index(xi: Int, zi: Int): Int = wrap(zi) * vertexSize + wrap(xi)

    // World coords
    val cellSize = sizeSettings.calculateCellSize(blockPos.lodLevel)
    val blockSize = sizeSettings.calculateBlockSize(blockPos)
    val (wxOffset, wzOffset) = sizeSettings.calculateTopLeft(blockPos, blockSize)
    val wxStart = wxOffset - cellSize
    val wzStart = wzOffset - cellSize
    var wx = wxStart
    var wz = wzStart
    val wxd = cellSize
    val wzd = cellSize

    // Texture coords
    val tud = (cellSize*tempTextureScale).toFloat
    val tvd = (cellSize*tempTextureScale).toFloat
    var tu = (wx*tempTextureScale).toFloat
    var tv = (wz*tempTextureScale).toFloat

    var i = 0
    var x: Int = 0
    var z: Int = 0
    while (z < vertexSize) {
      x = 0
      wx =  wxStart
      tu = (wx*tempTextureScale).toFloat
      while (x < vertexSize) {

        // Point location
        val wy = terrainFunction.getHeight(wx, wz)
        vertices(i) = new Vector3f(wx.toFloat, wy.toFloat, wz.toFloat)

        // Texture location
        //texCoords(i) = new Vector2f(tu, tv)
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


    require(index( 0,0) == 0)
    require(index( 1,0) == 1)
    require(index(-1,0) == vertexSize-1)
    require(index(-2,0) == vertexSize-2)
    require(index(-1,-1) == vertexSize*vertexSize-1)

    // Calculate normals
    val invCellSize = (1.0 / cellSize).toFloat
    var xn = 0
    var zn = 1
    var normalIndex = 0
    while (zn < vertexSize - 1) {
      xn = 1
      normalIndex = zn * vertexSize + xn
      while (xn < vertexSize - 1) {
        
        val xd = vertices(normalIndex + 1).y           - vertices(normalIndex - 1).y
        val zd = vertices(normalIndex + vertexSize).y  - vertices(normalIndex - vertexSize).y

        normals(normalIndex) = new Vector3f(-xd * invCellSize, 2, zd * invCellSize).normalizeLocal()

        xn += 1
        normalIndex += 1
      }
      zn += 1
    }


    // Sleeves
    val edgeStartX = wxOffset
    val edgeStartZ = wzOffset
    val edgeEndX   = wxOffset + blockSize
    val edgeEndZ   = wzOffset + blockSize

    val sleeveDownPull = (cellSize * sleeveDownPullFactor).toFloat

    def copySleeveVertex(targetX: Int, targetZ: Int, sourceX : Int, sourceZ : Int, posX: Double, posZ: Double) {
      vertices(index(targetX, targetZ)) = new Vector3f(posX.toFloat, vertices(index(sourceX, sourceZ)).y - sleeveDownPull, posZ.toFloat)
      normals(index(targetX, targetZ)) = normals(index(sourceX, sourceZ))
    }

    z = 0
    wz = edgeStartZ
    while (z < vertexSize) {
      copySleeveVertex( 0, z, 1, z, edgeStartX, wz)
      copySleeveVertex(-1, z,-2, z, edgeEndX, wz)
      z += 1
      wz += wzd
    }

    x = 0
    wx = edgeStartX
    while (x < vertexSize) {
      copySleeveVertex( x,  0, x,  1, wx, edgeStartZ)
      copySleeveVertex( x, -1, x, -2, wx, edgeEndZ)
      x += 1
      wx += wxd
    }

    // Sleeve corners
    /*
    copySleeveVertex( 0,  0,  1,  1, edgeStartX, edgeStartZ)
    copySleeveVertex(-1,  0, -2,  1, edgeEndX,   edgeStartZ)
    copySleeveVertex( 0, -1,  1, -2, edgeStartX, edgeEndZ)
    copySleeveVertex(-1, -1, -2, -2, edgeEndX,   edgeEndZ)
    */


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
    val sleevedCellCount = cellCount + 2
    val indexCount = 2 + sleevedCellCount * sleevedCellCount * 2 + (sleevedCellCount - 1) * 3
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
    addIndex(vertexSize)

    while (zCell < sleevedCellCount) {
      xCell = 0
      while (xCell < sleevedCellCount) {

        // From left to right on even rows, right to left on odd rows
        val vertexIndex = if (zCell % 2 == 0) vertexSize * zCell + xCell + 1 else vertexSize * zCell + vertexSize - xCell - 2

        addIndex(vertexIndex)
        addIndex(vertexIndex + vertexSize)

        xCell += 1
      }

      zCell += 1

      if (zCell < sleevedCellCount) {
        // Add some empty degenerate triangles to tie together consecutive rows
        val vertexIndex = if (zCell % 2 == 0) zCell * vertexSize else zCell * vertexSize + vertexSize - 1

        addIndex(vertexIndex)
        addIndex(vertexIndex)
        addIndex(vertexIndex + vertexSize)
      }
    }


    assert(indexCount == triangleIndex, "Triangle index ("+triangleIndex+") should equal the calculated index count "+indexCount)


    // Store in buffer
    mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices: _*));
    mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoords: _*));
    mesh.setBuffer(Type.Index,    1, BufferUtils.createIntBuffer(indexes: _*));
    mesh.setBuffer(Type.Normal,   3, BufferUtils.createFloatBuffer(normals: _*));
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
