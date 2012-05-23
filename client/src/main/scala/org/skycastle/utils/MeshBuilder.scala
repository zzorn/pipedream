package org.skycastle.utils

import com.jme3.scene.{VertexBuffer, Mesh}
import java.util.ArrayList
import collection.immutable.Stack
import com.jme3.math._

/**
 * Utility class for building custom JME meshes.
 */
class MeshBuilder {

  private val vertices = new ArrayList[Vector3f]()
  private val normals = new ArrayList[Vector3f]()
  private val texels = new ArrayList[Vector2f]()
  private val colors = new ArrayList[ColorRGBA]()

  private val indices = new ArrayList[Int]()
  
  private var normalsUsed = false
  private var texelsUsed = false
  private var colorsUsed = false

  private var nextVertex = 0
  private var nextIndex = 0

  private var transforms: Stack[Matrix4f] = Stack()
  private var currentTransformation: Matrix4f = new Matrix4f()
  
  def pushTransform(transformation: Matrix4f) {
    transforms = transforms.push(currentTransformation)
    currentTransformation = currentTransformation.mult(transformation)
  }
  
  def popTransform() {
    if (transforms.isEmpty) throw new IllegalStateException("Can not pop transformation, no transformations present!")

    currentTransformation = transforms.top
    transforms = transforms.pop
  }

  def getNextVertex = nextVertex
  def getNextIndex = nextIndex

  def addVertex(pos: Vector3f, normal: Vector3f = null, texel: Vector2f = null, color: ColorRGBA = null): Int = {
    val currentVertex = nextVertex

    val transformedPos: Vector3f = currentTransformation.mult(pos)

    vertices.add(transformedPos)
    normals.add(normal)
    texels.add(texel)
    colors.add(color)

    normalsUsed = normalsUsed || normal != null
    texelsUsed = texelsUsed || texel != null
    colorsUsed = colorsUsed || color != null

    nextVertex += 1
    currentVertex
  }

  /**
   * Creates a triangle.
   * Parameters are indexes of vertexes making up the triangle corners, in counter clockwise order.
   */
  def addTriangle(index0: Int, index1: Int, index2: Int) {
    indices.add(index0)
    indices.add(index1)
    indices.add(index2)
    nextIndex += 3
  }

  /**
   * Creates a quad.
   * Parameters are indexes of vertexes making up the quad corners, in counter clockwise order.
   */
  def addQuad(index0: Int, index1: Int, index2: Int, index3: Int) {
    addTriangle(index0, index1, index2)
    addTriangle(index2, index3, index0)
  }

  /*
  def addTriangleFan(index0: Int, index1: Int) {
    indices.add(index0)
    indices.add(index1)
    nextIndex += 2
  }
  
  def addTriangleStrip(index0: Int) {
    indices.add(index0)
    nextIndex += 1
  }
  */


  /**
   * Calculates normals from all non-empty triangles.
   */
  def normalize() {
    // NOTE: Assumes the triangles are added one by one, not as a strip or fan.

    // Initialize normals to zero
    var ni = 0
    while (ni < vertices.size) {
      normals.set(ni, new Vector3f())
      ni += 1
    }

    // Calculate normal for each triangle
    var i = 0
    val sideAB = new Vector3f()
    val sideAC = new Vector3f()
    while (i < indices.size) {
      val ai = indices.get(i)
      val bi = indices.get(i + 1)
      val ci = indices.get(i + 2)
      val a = vertices.get(ai)
      val b = vertices.get(bi)
      val c = vertices.get(ci)

      b.subtract(a, sideAB)
      c.subtract(a, sideAC)

      // Triangles with no surface area do not affect the normals of nearby sides
      val normal = sideAB.cross(sideAC)
      if (normal.lengthSquared > 0) {
        normals.get(ai).addLocal(normal) 
        normals.get(bi).addLocal(normal) 
        normals.get(ci).addLocal(normal) 
      }

      i += 3
    }

    // Normalize
    var normalIndex = 0
    while (normalIndex < normals.size) {
      val normal = normals.get(normalIndex)
      normal.normalizeLocal
      normalIndex += 1
    }

    if (vertices.size > 0) normalsUsed = true
  }

  
  def createMesh(calculateNormals: Boolean = true, clearAfterwards: Boolean = true): Mesh = {

    if (calculateNormals) normalize()

    val mesh = new Mesh()
    mesh.setBuffer(                 VertexBuffer.Type.Index,    1, BufferConverters.createIntBuffer(indices))
    mesh.setBuffer(                 VertexBuffer.Type.Position, 3, BufferConverters.createVector3fBuffer(vertices))
    if (normalsUsed) mesh.setBuffer(VertexBuffer.Type.Normal,   3, BufferConverters.createVector3fBuffer(normals))
    if (texelsUsed)  mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferConverters.createVector2fBuffer(texels))
    if (colorsUsed)  mesh.setBuffer(VertexBuffer.Type.Color,    4, BufferConverters.createColorRGBABuffer(colors))

    if (clearAfterwards) clear()

    mesh
  }

  
  def clear() {
    vertices.clear()
    normals.clear()
    texels.clear()
    colors.clear()
    indices.clear()
    
    nextVertex = 0
    nextIndex = 0
    
    normalsUsed = false
    texelsUsed = false
    colorsUsed = false
 }

}