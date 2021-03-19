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
import com.zynaps.parallax.math.Vector2
import com.zynaps.parallax.math.Vector3
import java.lang.Math.fma

@Suppress("DuplicatedCode")
internal data class Vertex(
    var vx: Float = 0.0F, var vy: Float = 0.0F, var vz: Float = 0.0F, var vw: Float = 0.0F,
    var nx: Float = 0.0F, var ny: Float = 0.0F, var nz: Float = 0.0F,
    var tu: Float = 0.0F, var tv: Float = 0.0F
) {

    constructor(v: Vector3, n: Vector3, t: Vector2) : this(v.x, v.y, v.z, 0.0F, n.x, n.y, n.z, t.x, t.y)

    fun copyFrom(other: Vertex) {
        vx = other.vx
        vy = other.vy
        vz = other.vz
        vw = other.vw
        nx = other.nx
        ny = other.ny
        nz = other.nz
        tu = other.tu
        tv = other.tv
    }

    fun transform(offset: Int, src: FloatArray, world: Matrix4, normal: Matrix4) {
        val x = src[offset + 0]
        val y = src[offset + 1]
        val z = src[offset + 2]
        val a = src[offset + 3]
        val b = src[offset + 4]
        val c = src[offset + 5]
        tu = src[offset + 6]
        tv = src[offset + 7]
        vx = fma(world.m00, x, fma(world.m01, y, fma(world.m02, z, world.m03)))
        vy = fma(world.m10, x, fma(world.m11, y, fma(world.m12, z, world.m13)))
        vz = fma(world.m20, x, fma(world.m21, y, fma(world.m22, z, world.m23)))
        vw = fma(world.m30, x, fma(world.m31, y, fma(world.m32, z, world.m33)))
        nx = fma(normal.m00, a, fma(normal.m01, b, normal.m02 * c))
        ny = fma(normal.m10, a, fma(normal.m11, b, normal.m12 * c))
        nz = fma(normal.m20, a, fma(normal.m21, b, normal.m22 * c))
    }

    fun lerp(a: Vertex, b: Vertex, t: Float) {
        vx = fma(t, b.vx - a.vx, a.vx)
        vy = fma(t, b.vy - a.vy, a.vy)
        vz = fma(t, b.vz - a.vz, a.vz)
        vw = fma(t, b.vw - a.vw, a.vw)
        nx = fma(t, b.nx - a.nx, a.nx)
        ny = fma(t, b.ny - a.ny, a.ny)
        nz = fma(t, b.nz - a.nz, a.nz)
        tu = fma(t, b.tu - a.tu, a.tu)
        tv = fma(t, b.tv - a.tv, a.tv)
    }

    fun toScreen(width: Int, height: Int) {
        vw = 1.0F / vw
        vx = 0.5F * fma(vx, vw, 1.0F) * width
        vy = 0.5F * fma(-vy, vw, 1.0F) * height
        vz = 0.5F * fma(vz, vw, 1.0F)
        nx *= vw
        ny *= vw
        nz *= vw
        tu *= vw
        tv *= vw
    }
}
