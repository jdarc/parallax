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

data class Vector2(val x: Float, val y: Float) {

    constructor(value: Float) : this(value, value)

    constructor(src: FloatArray, offset: Int = 0) : this(src[offset + 0], src[offset + 1])

    val length get() = sqrt(lengthSquared)

    val lengthSquared get() = (x * x) + (y * y)

    operator fun unaryMinus() = Vector2(-x, -y)

    operator fun plus(rhs: Float) = Vector2(x + rhs, y + rhs)

    operator fun plus(rhs: Vector2) = Vector2(x + rhs.x, y + rhs.y)

    operator fun minus(rhs: Float) = Vector2(x - rhs, y - rhs)

    operator fun minus(rhs: Vector2) = Vector2(x - rhs.x, y - rhs.y)

    operator fun times(rhs: Float) = Vector2(x * rhs, y * rhs)

    operator fun times(rhs: Vector2) = Vector2(x * rhs.x, y * rhs.y)

    operator fun times(rhs: Matrix3) = Vector2((x * rhs.m00) + (y * rhs.m10) + rhs.m20, (x * rhs.m01) + (y * rhs.m11) + rhs.m21)

    operator fun div(rhs: Float) = Vector2(x / rhs, y / rhs)

    operator fun div(rhs: Vector2) = Vector2(x / rhs.x, y / rhs.y)

    operator fun get(index: Int) = when (index) {
        0 -> x; 1 -> y; else -> throw IndexOutOfBoundsException()
    }

    fun abs() = Vector2(abs(x), abs(y))

    fun angle(rhs: Vector2) = acos((dot(rhs) / (length * rhs.length)).coerceIn(-1F, 1F))

    fun clamp(min: Float, max: Float) = Vector2(x.coerceIn(min, max), y.coerceIn(min, max))

    fun clamp(min: Vector2, max: Vector2) = Vector2(x.coerceIn(min.x, max.x), y.coerceIn(min.y, max.y))

    fun clampMax(max: Float) = Vector2(Scalar.min(x, max), Scalar.min(y, max))

    fun clampMin(min: Float) = Vector2(Scalar.max(x, min), Scalar.max(y, min))

    fun distance(rhs: Vector2) = sqrt(distanceSquared(rhs))

    fun distanceSquared(rhs: Vector2) = sqr(x - rhs.x) + sqr(y - rhs.y)

    fun dot(rhs: Vector2) = (x * rhs.x) + (y * rhs.y)

    fun equals(rhs: Vector2, epsilon: Float = EPSILON) = Scalar.equals(x, rhs.x, epsilon) && Scalar.equals(y, rhs.y, epsilon)

    fun lerp(rhs: Vector2, alpha: Float): Vector2 {
        val t = 1F - alpha
        val x1 = (x * t) + (rhs.x * alpha)
        val y1 = (y * t) + (rhs.y * alpha)
        return Vector2(x1, y1)
    }

    fun max(rhs: Vector2) = Vector2(Scalar.max(x, rhs.x), Scalar.max(y, rhs.y))

    fun min(rhs: Vector2) = Vector2(Scalar.min(x, rhs.x), Scalar.min(y, rhs.y))

    fun normalize() = this * (1F / length)

    fun reflect(normal: Vector2) = Vector2(x - normal.x * (2F * (x * x + y * y)), y - normal.y * (2F * (x * x + y * y)))

    fun squareRoot() = Vector2(sqrt(x), sqrt(y))

    fun transformNormal(matrix: Matrix3): Vector2 {
        val x1 = (x * matrix.m00) + (y * matrix.m10)
        val y1 = (x * matrix.m01) + (y * matrix.m11)
        return Vector2(x1, y1)
    }

    fun toArray(dst: FloatArray = FloatArray(2), offset: Int = 0) {
        dst[offset + 0] = x; dst[offset + 1] = y
    }

    companion object {
        val ZERO = Vector2(0F)

        val ONE = Vector2(1F)
        val MINUS_ONE = Vector2(-1F)

        val UNIT_X = Vector2(1F, 0F)
        val UNIT_Y = Vector2(0F, 1F)

        val MINUS_UNIT_X = Vector2(-1F, 0F)
        val MINUS_UNIT_Y = Vector2(0F, -1F)

        val POSITIVE_INFINITY = Vector2(Float.POSITIVE_INFINITY)
        val NEGATIVE_INFINITY = Vector2(Float.NEGATIVE_INFINITY)
    }
}
