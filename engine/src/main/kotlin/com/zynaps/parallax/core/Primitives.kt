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

import com.zynaps.parallax.math.Matrix4
import com.zynaps.parallax.math.Scalar
import com.zynaps.parallax.math.Vector3
import java.util.*

object Primitives {

    fun plane(texture: Material) = plane(1.0F, 1.0F, texture)

    fun plane(width: Float, depth: Float, material: Material): Assembler {
        val factory = Assembler()
        factory.name = "Plane"
        factory.group = "Plane"

        factory.vertex(-width * 0.5F, 0.0F, depth * 0.5F)
        factory.vertex(width * 0.5F, 0.0F, depth * 0.5F)
        factory.vertex(width * 0.5F, 0.0F, -depth * 0.5F)
        factory.vertex(-width * 0.5F, 0.0F, -depth * 0.5F)

        factory.normal(0.0F, 1.0F, 0.0F)

        factory.uv(0.0F, 0.0F)
        factory.uv(1.0F, 0.0F)
        factory.uv(1.0F, 1.0F)
        factory.uv(0.0F, 1.0F)

        factory.triangle(0, 1, 2).normals(0, 0, 0).uvs(0, 1, 2).material = material
        factory.triangle(2, 3, 0).normals(0, 0, 0).uvs(2, 3, 0).material = material

        return factory
    }

    fun cube(material: Material) = cube(Vector3(-0.5F, -0.5F, -0.5F), Vector3(0.5F, 0.5F, 0.5F), material)

    fun cube(min: Vector3, max: Vector3, material: Material): Assembler {
        val factory = Assembler()
        factory.name = "Cube"
        factory.group = "Cube"

        factory.vertex(min.x, max.y, min.z)
        factory.vertex(max.x, max.y, min.z)
        factory.vertex(max.x, min.y, min.z)
        factory.vertex(min.x, min.y, min.z)
        factory.vertex(max.x, max.y, max.z)
        factory.vertex(min.x, max.y, max.z)
        factory.vertex(min.x, min.y, max.z)
        factory.vertex(max.x, min.y, max.z)

        factory.normal(0.0F, 0.0F, -1.0F)
        factory.normal(1.0F, 0.0F, 0.0F)
        factory.normal(0.0F, 0.0F, 1.0F)
        factory.normal(-1.0F, 0.0F, 0.0F)
        factory.normal(0.0F, 1.0F, 0.0F)
        factory.normal(0.0F, -1.0F, 0.0F)

        factory.uv(0.0F, 0.0F)
        factory.uv(1.0F, 0.0F)
        factory.uv(1.0F, 1.0F)
        factory.uv(0.0F, 1.0F)

        factory.triangle(0, 1, 2).normals(0, 0, 0).uvs(0, 1, 2).material = material
        factory.triangle(2, 3, 0).normals(0, 0, 0).uvs(2, 3, 0).material = material
        factory.triangle(1, 4, 7).normals(1, 1, 1).uvs(0, 1, 2).material = material
        factory.triangle(7, 2, 1).normals(1, 1, 1).uvs(2, 3, 0).material = material
        factory.triangle(4, 5, 6).normals(2, 2, 2).uvs(0, 1, 2).material = material
        factory.triangle(6, 7, 4).normals(2, 2, 2).uvs(2, 3, 0).material = material
        factory.triangle(0, 3, 6).normals(3, 3, 3).uvs(0, 1, 2).material = material
        factory.triangle(6, 5, 0).normals(3, 3, 3).uvs(2, 3, 0).material = material
        factory.triangle(0, 5, 4).normals(4, 4, 4).uvs(0, 1, 2).material = material
        factory.triangle(4, 1, 0).normals(4, 4, 4).uvs(2, 3, 0).material = material
        factory.triangle(3, 2, 7).normals(5, 5, 5).uvs(0, 1, 2).material = material
        factory.triangle(7, 6, 3).normals(5, 5, 5).uvs(2, 3, 0).material = material

        return factory
    }

    fun sphere(material: Material) = sphere(0.5F, 32, 32, material)

    fun sphere(radius: Float, stacks: Int, slices: Int, material: Material): Assembler {
        val factory = Assembler()
        factory.name = "Sphere"
        factory.group = "Sphere"

        val stackAngle = Scalar.PI / stacks
        val sliceAngle = Scalar.PI / slices * 2.0F

        val curve = ArrayList<Vector3>()

        for (stack in 0..stacks) curve.add(Matrix4.createRotationZ(stackAngle * stack) * Vector3(0.0F, 1.0F, 0.0F))

        for (slice in 0..slices) {
            val aboutY = Matrix4.createRotationY(sliceAngle * slice)
            for ((v, point) in curve.withIndex()) {
                val vertex = aboutY * point
                factory.vertex(vertex.x * radius, vertex.y * radius, vertex.z * radius)
                factory.normal(vertex.x, vertex.y, vertex.z)
                factory.uv(slice / slices.toFloat(), v / curve.size.toFloat())
            }
        }

        for (slice in 0 until slices) {
            val tindex = slice * (stacks + 1)
            for (stack in 0 until stacks) {
                val ma = stack + tindex
                val mb = stack + tindex + 1
                val mc = stack + tindex + (stacks + 1) + 1
                val md = stack + tindex + (stacks + 1)
                factory.triangle(md, ma, mb).normals(md, ma, mb).uvs(md, ma, mb).material = material
                factory.triangle(mb, mc, md).normals(mb, mc, md).uvs(mb, mc, md).material = material
            }
        }

        return factory
    }

    fun capsule(material: Material) = capsule(0.5F, 1.0F, 32, 32, material)

    fun capsule(radius: Float, height: Float, stacks: Int, slices: Int, material: Material): Assembler {
        val factory = Assembler()
        factory.name = "Capsule"
        factory.group = "Capsule"

        val stackAngle = Scalar.PI / stacks
        val sliceAngle = Scalar.PI / slices * 2.0F

        val curve = ArrayList<Vector3>()
        val normals = ArrayList<Vector3>()

        for (stack in 0..stacks / 2) {
            val temp = Matrix4.createRotationZ(stackAngle * stack)
            curve.add(temp * Vector3(0.0F, radius, 0.0F) + Vector3(0.0F, height * 0.5F, 0.0F))
            normals.add(temp * Vector3(0.0F, 1.0F, 0.0F))
        }
        for (stack in stacks / 2..stacks) {
            val temp = Matrix4.createRotationZ(stackAngle * stack)
            curve.add(temp * Vector3(0.0F, radius, 0.0F) + Vector3(0.0F, -height * 0.5F, 0.0F))
            normals.add(temp * Vector3(0.0F, 1.0F, 0.0F))
        }

        for (slice in 0..slices) {
            val aboutY = Matrix4.createRotationY(sliceAngle * slice)
            for ((v, index) in curve.indices.withIndex()) {
                val vertex = aboutY * curve[index]
                val normal = aboutY * normals[index]
                factory.vertex(vertex.x, vertex.y, vertex.z)
                factory.normal(normal.x, normal.y, normal.z)
                factory.uv(slice / slices.toFloat(), v / curve.size.toFloat())
            }
        }

        for (slice in 0 until slices) {
            var tindex = slice * (stacks + 2)
            for (stack in 0..stacks) {
                val ma = tindex
                val mb = tindex + 1
                val mc = tindex + (stacks + 2) + 1
                val md = tindex + (stacks + 2)
                val tri0 = factory.triangle(md, ma, mb).normals(md, ma, mb).uvs(md, ma, mb)
                val tri1 = factory.triangle(mb, mc, md).normals(mb, mc, md).uvs(mb, mc, md)
                tri0.material = material
                tri1.material = material
                tindex++
            }
        }

        return factory
    }

    fun terrain(width: Float, depth: Float, height: Float, resx: Int, resz: Int, material: Material): Assembler {
        val h = HeightField(resx, resz)
        h.fractalize()
        h.smooth()
        h.canyonize()
        h.smooth()
        val factory = Assembler()
        factory.name = "Terrain"
        factory.group = "Terrain"
        val halfWidth = -width / 2.0F
        val halfDepth = -depth / 2.0F
        val deltaX = width / resx
        val deltaZ = depth / resz
        val ux = 1.0F / resx
        val uz = 1.0F / resz
        val heights = Array(resz) { FloatArray(resx) }
        for (z in 0 until resz) {
            val vz = depth * (halfDepth + z * deltaZ) / resz
            for (x in 0 until resx) {
                val vx = width * (halfWidth + x * deltaX) / resx
                heights[z][x] = height * h.getItem(x, z) / 0xFF
                val vy = heights[z][x]
                factory.vertex(vx, vy, vz)
                factory.uv(ux * x, uz * z)
            }
        }
        val xx = deltaX * width / resx
        val zz = deltaZ * depth / resz
        for (z in 0 until resz) {
            var z1 = z - 1
            if (z1 < 0) {
                z1 = 0
            }
            var z2 = z + 1
            if (z2 >= resz) {
                z2 = resz - 2
            }
            for (x in 0 until resx) {
                var x1 = x - 1
                var x2 = x + 1
                if (x1 < 0) {
                    x1 = 0
                }
                if (x2 >= resx) {
                    x2 = resx - 2
                }
                val va = Vector3(-xx, heights[z][x1], 0.0F)
                val vb = Vector3(0.0F, heights[z1][x], -zz)
                val vo = Vector3(0.0F, heights[z][x], 0.0F)
                val vc = Vector3(xx, heights[z][x2], 0.0F)
                val vd = Vector3(0.0F, heights[z2][x], zz)
                var norm = (vd - vo).cross(vc - vo) + (vc - vo).cross(vb - vo) + (vb - vo).cross(va - vo) + (va - vo).cross(vd - vo)
                norm = norm.normalize()
                factory.normal(norm.x, norm.y, norm.z)
            }
        }
        for (z in 0 until resz - 1) {
            val off = z * resx
            for (x in 0 until resx - 1) {
                val a = off + x
                val b = a + 1
                val c = b + resx
                val d = c - 1
                factory.triangle(a, d, c).normals(a, d, c).uvs(a, d, c).material = material
                factory.triangle(c, b, a).normals(c, b, a).uvs(c, b, a).material = material
            }
        }
        return factory
    }
}
