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

import com.zynaps.parallax.math.Scalar.sqr
import com.zynaps.parallax.math.Scalar.sqrt
import com.zynaps.parallax.math.Vector3.Companion.NEGATIVE_INFINITY
import com.zynaps.parallax.math.Vector3.Companion.POSITIVE_INFINITY

data class Aabb(private var min: Vector3 = POSITIVE_INFINITY, private var max: Vector3 = NEGATIVE_INFINITY) {

    val width get() = max.x - min.x
    val height get() = max.y - min.y
    val depth get() = max.z - min.z

    val center get() = Vector3((min.x + max.x) * 0.5F, (min.y + max.y) * 0.5F, (min.z + max.z) * 0.5F)
    val radius get() = sqrt(sqr(max.x - min.x) + sqr(max.y - min.y) + sqr(max.z - min.z)) * 0.5F

    val minimum get() = min
    val maximum get() = max

    fun reset(): Aabb {
        min = POSITIVE_INFINITY
        max = NEGATIVE_INFINITY
        return this
    }

    fun contains(v: Vector3) =
        (v.x >= min.x) && (v.y >= min.y) && (v.z >= min.z) &&
        (v.x <= max.x) && (v.y <= max.y) && (v.z <= max.z)

    fun pointsBehind(plane: Plane) =
        (if (plane.dot(min.x, max.y, min.z) < 0) 1 else 0) +
        (if (plane.dot(max.x, max.y, min.z) < 0) 1 else 0) +
        (if (plane.dot(max.x, min.y, min.z) < 0) 1 else 0) +
        (if (plane.dot(min.x, min.y, min.z) < 0) 1 else 0) +
        (if (plane.dot(min.x, max.y, max.z) < 0) 1 else 0) +
        (if (plane.dot(max.x, max.y, max.z) < 0) 1 else 0) +
        (if (plane.dot(max.x, min.y, max.z) < 0) 1 else 0) +
        (if (plane.dot(min.x, min.y, max.z) < 0) 1 else 0)

    fun aggregate(x: Float, y: Float, z: Float): Aabb {
        if (x.isFinite() && y.isFinite() && z.isFinite()) {
            min = min.min(x, y, z)
            max = max.max(x, y, z)
        }
        return this
    }

    fun aggregate(v: Vector3) = aggregate(v.x, v.y, v.z)

    fun aggregate(box: Aabb) =
        aggregate(box.minimum.x, box.minimum.y, box.minimum.z).aggregate(box.maximum.x, box.maximum.y, box.maximum.z)

    fun aggregate(box: Aabb, matrix: Matrix4): Aabb {
        val a = matrix.m00 * box.minimum.x
        val b = matrix.m10 * box.minimum.x
        val c = matrix.m20 * box.minimum.x
        val d = matrix.m01 * box.minimum.y
        val e = matrix.m11 * box.minimum.y
        val f = matrix.m21 * box.minimum.y
        val g = matrix.m02 * box.minimum.z
        val h = matrix.m12 * box.minimum.z
        val i = matrix.m22 * box.minimum.z
        val j = matrix.m00 * box.maximum.x
        val k = matrix.m10 * box.maximum.x
        val l = matrix.m20 * box.maximum.x
        val m = matrix.m01 * box.maximum.y
        val n = matrix.m11 * box.maximum.y
        val o = matrix.m21 * box.maximum.y
        val p = matrix.m02 * box.maximum.z
        val q = matrix.m12 * box.maximum.z
        val r = matrix.m22 * box.maximum.z
        aggregate(a + m + g + matrix.m03, b + n + h + matrix.m13, c + o + i + matrix.m23)
        aggregate(j + m + g + matrix.m03, k + n + h + matrix.m13, l + o + i + matrix.m23)
        aggregate(j + d + g + matrix.m03, k + e + h + matrix.m13, l + f + i + matrix.m23)
        aggregate(a + d + g + matrix.m03, b + e + h + matrix.m13, c + f + i + matrix.m23)
        aggregate(a + m + p + matrix.m03, b + n + q + matrix.m13, c + o + r + matrix.m23)
        aggregate(j + m + p + matrix.m03, k + n + q + matrix.m13, l + o + r + matrix.m23)
        aggregate(j + d + p + matrix.m03, k + e + q + matrix.m13, l + f + r + matrix.m23)
        aggregate(a + d + p + matrix.m03, b + e + q + matrix.m13, c + f + r + matrix.m23)
        return this
    }
}
