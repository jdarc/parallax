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
import com.zynaps.parallax.math.Scalar.abs
import com.zynaps.parallax.math.Scalar.acos
import com.zynaps.parallax.math.Scalar.sqr
import com.zynaps.parallax.math.Scalar.sqrt

@Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode", "SpellCheckingInspection", "NOTHING_TO_INLINE")
data class Vector2(inline val x: Float, inline val y: Float) {

    constructor(value: Float) : this(value, value)

    constructor(src: FloatArray, offset: Int = 0) : this(src[offset + 0], src[offset + 1])

    inline val length get() = sqrt(lengthSquared)

    inline val lengthSquared get() = (x * x) + (y * y)

    inline operator fun unaryMinus() = Vector2(-x, -y)

    inline operator fun plus(rhs: Float) = Vector2(x + rhs, y + rhs)

    inline operator fun plus(rhs: Vector2) = Vector2(x + rhs.x, y + rhs.y)

    inline operator fun minus(rhs: Float) = Vector2(x - rhs, y - rhs)

    inline operator fun minus(rhs: Vector2) = Vector2(x - rhs.x, y - rhs.y)

    inline operator fun times(rhs: Float) = Vector2(x * rhs, y * rhs)

    inline operator fun times(rhs: Vector2) = Vector2(x * rhs.x, y * rhs.y)

    inline operator fun times(rhs: Matrix3): Vector2 {
        val x1 = (x * rhs.m00) + (y * rhs.m10) + rhs.m20
        val y1 = (x * rhs.m01) + (y * rhs.m11) + rhs.m21
        return Vector2(x1, y1)
    }

    inline operator fun div(rhs: Float) = Vector2(x / rhs, y / rhs)

    inline operator fun div(rhs: Vector2) = Vector2(x / rhs.x, y / rhs.y)

    inline operator fun get(index: Int) = when (index) {
        0 -> x; 1 -> y; else -> throw IndexOutOfBoundsException()
    }

    inline fun abs() = Vector2(abs(x), abs(y))

    inline fun angle(rhs: Vector2) = acos((dot(rhs) / (length * rhs.length)).coerceIn(-1.0F, 1.0F))

    inline fun clamp(min: Float, max: Float) = Vector2(x.coerceIn(min, max), y.coerceIn(min, max))

    inline fun clamp(min: Vector2, max: Vector2) = Vector2(x.coerceIn(min.x, max.x), y.coerceIn(min.y, max.y))

    inline fun clampMax(max: Float) = Vector2(Scalar.min(x, max), Scalar.min(y, max))

    inline fun clampMin(min: Float) = Vector2(Scalar.max(x, min), Scalar.max(y, min))

    inline fun distance(rhs: Vector2) = sqrt(distanceSquared(rhs))

    inline fun distanceSquared(rhs: Vector2) = sqr(x - rhs.x) + sqr(y - rhs.y)

    inline fun dot(rhs: Vector2) = (x * rhs.x) + (y * rhs.y)

    inline fun equals(rhs: Vector2, epsilon: Float = EPSILON) = Scalar.equals(x, rhs.x, epsilon) && Scalar.equals(y, rhs.y, epsilon)

    inline fun lerp(rhs: Vector2, alpha: Float): Vector2 {
        val t = 1.0F - alpha
        val x1 = (x * t) + (rhs.x * alpha)
        val y1 = (y * t) + (rhs.y * alpha)
        return Vector2(x1, y1)
    }

    inline fun max(rhs: Vector2) = Vector2(Scalar.max(x, rhs.x), Scalar.max(y, rhs.y))

    inline fun min(rhs: Vector2) = Vector2(Scalar.min(x, rhs.x), Scalar.min(y, rhs.y))

    inline fun normalize() = this * (1.0F / length)

    inline fun reflect(normal: Vector2): Vector2 {
        val r = 2.0F * (x * x + y * y)
        val x1 = x - (normal.x * r)
        val y1 = y - (normal.y * r)
        return Vector2(x1, y1)
    }

    inline fun squareRoot() = Vector2(sqrt(x), sqrt(y))

    inline fun transformNormal(matrix: Matrix3): Vector2 {
        val x1 = (x * matrix.m00) + (y * matrix.m10)
        val y1 = (x * matrix.m01) + (y * matrix.m11)
        return Vector2(x1, y1)
    }

    inline fun toArray(dst: FloatArray = FloatArray(2), offset: Int = 0) {
        dst[offset + 0] = x; dst[offset + 1] = y
    }

    companion object {

        val ZERO = Vector2(0.0F)

        val ONE = Vector2(1.0F)
        val MINUS_ONE = Vector2(-1.0F)

        val UNIT_X = Vector2(1.0F, 0.0F)
        val UNIT_Y = Vector2(0.0F, 1.0F)

        val MINUS_UNIT_X = Vector2(-1.0F, 0.0F)
        val MINUS_UNIT_Y = Vector2(0.0F, -1.0F)

        val POSITIVE_INFINITY = Vector2(Float.POSITIVE_INFINITY)
        val NEGATIVE_INFINITY = Vector2(Float.NEGATIVE_INFINITY)
    }
}
