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
package com.zynaps.parallax.math

import com.zynaps.parallax.math.Scalar.EPSILON

@Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode", "SpellCheckingInspection", "NOTHING_TO_INLINE")
data class Plane(inline val normal: Vector3, inline val distance: Float) {

    constructor(x: Float, y: Float, z: Float, d: Float) : this(Vector3(x, y, z), d)

    inline fun dot(x: Float, y: Float, z: Float) = (normal.x * x) + (normal.y * y) + (normal.z * z) + distance

    inline fun dot(rhs: Vector3) = (normal.x * rhs.x) + (normal.y * rhs.y) + (normal.z * rhs.z) + distance

    inline fun dot(rhs: Vector4) = (normal.x * rhs.x) + (normal.y * rhs.y) + (normal.z * rhs.z) + (distance * rhs.w)

    companion object {

        fun normalize(plane: Plane): Plane {
            val lenSqr = plane.normal.lengthSquared
            if (lenSqr < EPSILON) throw ArithmeticException(lenSqr.toString())
            val invLen = Scalar.invSqrt(lenSqr)
            val x = plane.normal.x * invLen
            val y = plane.normal.y * invLen
            val z = plane.normal.z * invLen
            val normal = Vector3(x, y, z)
            val distance = plane.distance * invLen
            return Plane(normal, distance)
        }

        fun create(x: Float, y: Float, z: Float, d: Float): Plane {
            val lenSqr = x * x + y * y + z * z
            if (lenSqr < EPSILON) throw ArithmeticException(lenSqr.toString())
            val invLen = Scalar.invSqrt(lenSqr)
            val normal = Vector3(x * invLen, y * invLen, z * invLen)
            val distance = d * invLen
            return Plane(normal, distance)
        }

        fun create(v0: Vector3, v1: Vector3, v2: Vector3): Plane {
            val ax = v1.x - v0.x
            val ay = v1.y - v0.y
            val az = v1.z - v0.z
            val bx = v2.x - v0.x
            val by = v2.y - v0.y
            val bz = v2.z - v0.z
            val nx = (ay * bz) - (az * by)
            val ny = (az * bx) - (ax * bz)
            val nz = (ax * by) - (ay * bx)
            val lenSqr = (nx * nx) + (ny * ny) + (nz * nz)
            if (lenSqr < EPSILON) throw ArithmeticException(lenSqr.toString())
            val invLen = Scalar.invSqrt(lenSqr)
            val normal = Vector3(nx * invLen, ny * invLen, nz * invLen)
            val distance = -normal.x * v0.x - normal.y * v0.y - normal.z * v0.z
            return Plane(normal, distance)
        }
    }
}
