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
import com.zynaps.parallax.math.Scalar.HALF
import com.zynaps.parallax.math.Scalar.ONE
import com.zynaps.parallax.math.Scalar.ZERO
import com.zynaps.parallax.math.Vector2
import com.zynaps.parallax.math.Vector3

internal data class Vertex(
    var vx: Float = ZERO, var vy: Float = ZERO, var vz: Float = ZERO, var vw: Float = ZERO,
    var nx: Float = ZERO, var ny: Float = ZERO, var nz: Float = ZERO,
    var tu: Float = ZERO, var tv: Float = ZERO
) {

    constructor(v: Vector3, n: Vector3, t: Vector2) : this(v.x, v.y, v.z, ZERO, n.x, n.y, n.z, t.x, t.y)

    fun set(other: Vertex) {
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
        vx = world.m00 * x + world.m01 * y + world.m02 * z + world.m03
        vy = world.m10 * x + world.m11 * y + world.m12 * z + world.m13
        vz = world.m20 * x + world.m21 * y + world.m22 * z + world.m23
        vw = world.m30 * x + world.m31 * y + world.m32 * z + world.m33
        nx = normal.m00 * a + normal.m01 * b + normal.m02 * c
        ny = normal.m10 * a + normal.m11 * b + normal.m12 * c
        nz = normal.m20 * a + normal.m21 * b + normal.m22 * c
    }

    fun lerp(a: Vertex, b: Vertex, t: Float) {
        vx = t * (b.vx - a.vx) + a.vx
        vy = t * (b.vy - a.vy) + a.vy
        vz = t * (b.vz - a.vz) + a.vz
        vw = t * (b.vw - a.vw) + a.vw
        nx = t * (b.nx - a.nx) + a.nx
        ny = t * (b.ny - a.ny) + a.ny
        nz = t * (b.nz - a.nz) + a.nz
        tu = t * (b.tu - a.tu) + a.tu
        tv = t * (b.tv - a.tv) + a.tv
    }

    fun scale(width: Int, height: Int) {
        vw = ONE / vw
        vx = HALF * (vx * vw + ONE) * width
        vy = HALF * (ONE - vy * vw) * height
        vz = HALF * (vz * vw + ONE)
        nx *= vw
        ny *= vw
        nz *= vw
        tu *= vw
        tv *= vw
    }
}
