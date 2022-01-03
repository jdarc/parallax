/*
 * Copyright (c) 2021 Jean d'Arc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.zynaps.parallax.core

import com.zynaps.parallax.math.Vector2
import com.zynaps.parallax.math.Vector3
import java.util.*

class Assembler {
    private val vertices = mutableListOf<Vector3>()
    private val vertexNormals = mutableListOf<Vector3>()
    private val triangleNormals = mutableListOf<Vector3>()
    private val textureCoordinates = mutableListOf<Vector2>()
    private val triangleGroups = mutableMapOf<String, MutableList<Triangle>>()

    var name = "object"
    var group = "group"

    val vertexCount get() = vertices.size
    val normalCount get() = vertexNormals.size
    val uvCount get() = textureCoordinates.size
    val triangleCount get() = triangleGroups.values.sumOf { it.size }

    fun clear() {
        vertices.clear()
        vertexNormals.clear()
        triangleNormals.clear()
        textureCoordinates.clear()
        triangleGroups.clear()
    }

    fun groups() = triangleGroups.keys.toTypedArray()

    fun groupByName(name: String): List<Triangle> = triangleGroups.getOrElse(name) { emptyList() }

    fun vertex(v: Vector3) = vertex(v.x, v.y, v.z)
    fun vertex(data: Array<Float>) = vertex(data[0], data[1], data[2])
    fun vertex(x: Float, y: Float, z: Float) {
        vertices.add(Vector3(x, y, z))
        vertexNormals.add(Vector3.ZERO)
    }

    fun normal(v: Vector3) = normal(v.x, v.y, v.z)
    fun normal(data: Array<Float>) = normal(data[0], data[1], data[2])
    fun normal(x: Float, y: Float, z: Float) {
        triangleNormals.add(Vector3(x, y, z).normalize())
    }

    fun uv(v: Vector3) = uv(v.x, v.y)
    fun uv(data: Array<Float>) = uv(data[0], data[1])
    fun uv(u: Float, v: Float) {
        textureCoordinates.add(Vector2(u, 1.0F - v))
    }

    fun triangle(a: Int, b: Int, c: Int): Triangle {
        val triangle = Triangle(a, b, c)
        triangleGroups.getOrPut(group) { mutableListOf() }.add(triangle)
        return triangle
    }

    fun compile(): Model {
        generateNormals()
        return Model(name, groupByMaterial())
    }

    private fun generateNormals() {
        vertexNormals.fill(Vector3.ZERO)

        triangleGroups.values.flatten().forEach {
            val a = vertices[it.v0]
            val b = vertices[it.v1]
            val c = vertices[it.v2]
            it.normal = (b - a).cross(c - b).normalize()
            vertexNormals[it.v0] += it.normal
            vertexNormals[it.v1] += it.normal
            vertexNormals[it.v2] += it.normal
        }

        (0 until vertexNormals.size).forEach { vertexNormals[it] = vertexNormals[it].normalize() }
    }

    private fun groupByMaterial(): MutableMap<Material, Mesh> {
        val buckets = HashMap<Material, MutableList<Triangle>>()
        triangleGroups.values.flatten().forEach { buckets.getOrPut(it.material) { ArrayList<Triangle>() }.add(it) }

        val groups = HashMap<Material, Mesh>()
        for ((key, value) in buckets) {
            val vb = ArrayList<Vertex>()
            for (triangle in value) {
                vb.add(toVertex(triangle, triangle.v0, triangle.n0, triangle.t0))
                vb.add(toVertex(triangle, triangle.v1, triangle.n1, triangle.t1))
                vb.add(toVertex(triangle, triangle.v2, triangle.n2, triangle.t2))
            }
            val vertices = LinkedHashMap<Vertex, Int>()
            val triangles = vb.indices.map { vertices.getOrPut(vb[it]) { vertices.size } }.toIntArray()
            groups[key] = Mesh(toBuffer(vertices.keys.toList()), triangles)
        }

        return groups
    }

    private fun toVertex(modifier: Triangle, vidx: Int, nidx: Int, tidx: Int) =
        Vertex(
            vertices[vidx],
            when {
                modifier.shading == Shading.SMOOTH && (vidx < vertexNormals.size) -> vertexNormals[vidx]
                modifier.shading == Shading.NORMAL && (nidx < triangleNormals.size) -> triangleNormals[nidx]
                else -> modifier.normal
            }, if (tidx < textureCoordinates.size) textureCoordinates[tidx] else Vector2.ZERO
        )

    private fun toBuffer(vertices: List<Vertex>) =
        vertices.flatMap { listOf(it.vx, it.vy, it.vz, it.nx, it.ny, it.nz, it.tu, it.tv) }.toFloatArray()
}
