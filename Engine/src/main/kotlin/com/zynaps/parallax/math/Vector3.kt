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
import com.zynaps.parallax.math.Scalar.max
import com.zynaps.parallax.math.Scalar.min
import com.zynaps.parallax.math.Scalar.sqr
import com.zynaps.parallax.math.Scalar.sqrt

@Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode", "SpellCheckingInspection", "NOTHING_TO_INLINE")
data class Vector3(inline val x: Float, inline val y: Float, inline val z: Float) {

    constructor(value: Float) : this(value, value, value)

    constructor(value: Vector2, z: Float) : this(value.x, value.y, z)

    constructor(src: FloatArray, offset: Int = 0) : this(src[offset + 0], src[offset + 1], src[offset + 2])

    inline val length get() = sqrt(lengthSquared)

    inline val lengthSquared get() = (x * x) + (y * y) + (z * z)

    inline operator fun unaryMinus() = Vector3(-x, -y, -z)

    inline operator fun plus(rhs: Float) = Vector3(x + rhs, y + rhs, z + rhs)

    inline operator fun plus(rhs: Vector3) = Vector3(x + rhs.x, y + rhs.y, z + rhs.z)

    inline operator fun minus(rhs: Float) = Vector3(x - rhs, y - rhs, z - rhs)

    inline operator fun minus(rhs: Vector3) = Vector3(x - rhs.x, y - rhs.y, z - rhs.z)

    inline operator fun times(rhs: Float) = Vector3(x * rhs, y * rhs, z * rhs)

    inline operator fun times(rhs: Vector3) = Vector3(x * rhs.x, y * rhs.y, z * rhs.z)

    inline operator fun times(rhs: Matrix3): Vector3 {
        val x1 = (x * rhs.m00) + (y * rhs.m10) + (z * rhs.m20)
        val y1 = (x * rhs.m01) + (y * rhs.m11) + (z * rhs.m21)
        val z1 = (x * rhs.m02) + (y * rhs.m12) + (z * rhs.m22)
        return Vector3(x1, y1, z1)
    }

    inline operator fun times(rhs: Matrix4): Vector3 {
        val x1 = (x * rhs.m00) + (y * rhs.m10) + (z * rhs.m20) + rhs.m30
        val y1 = (x * rhs.m01) + (y * rhs.m11) + (z * rhs.m21) + rhs.m31
        val z1 = (x * rhs.m02) + (y * rhs.m12) + (z * rhs.m22) + rhs.m32
        return Vector3(x1, y1, z1)
    }

    inline operator fun times(rhs: Quaternion): Vector3 {
        val rx = rhs.x + rhs.x
        val ry = rhs.y + rhs.y
        val rz = rhs.z + rhs.z
        val wrx = rhs.w * rx
        val wry = rhs.w * ry
        val wrz = rhs.w * rz
        val xrx = rhs.x * rx
        val xry = rhs.x * ry
        val xrz = rhs.x * rz
        val yry = rhs.y * ry
        val yrz = rhs.y * rz
        val zrz = rhs.z * rz
        val x1 = x * (1.0F - yry - zrz) + y * (xry - wrz) + z * (xrz + wry)
        val y1 = x * (xry + wrz) + y * (1.0F - xrx - zrz) + z * (yrz - wrx)
        val z1 = x * (xrz - wry) + y * (yrz + wrx) + z * (1.0F - xrx - yry)
        return Vector3(x1, y1, z1)
    }

    inline operator fun div(rhs: Float) = Vector3(x / rhs, y / rhs, z / rhs)

    inline operator fun div(rhs: Vector3) = Vector3(x / rhs.x, y / rhs.y, z / rhs.z)

    inline operator fun get(index: Int) = when (index) {
        0 -> x; 1 -> y; 2 -> z; else -> throw IndexOutOfBoundsException()
    }

    inline fun angle(rhs: Vector3) = acos((dot(rhs) / (length * rhs.length)).coerceIn(-1.0F, 1.0F))

    inline fun abs() = Vector3(abs(x), abs(y), abs(z))

    inline fun clamp(min: Float, max: Float) = Vector3(x.coerceIn(min, max), y.coerceIn(min, max), z.coerceIn(min, max))

    inline fun clamp(min: Vector3, max: Vector3) = Vector3(x.coerceIn(min.x, max.x), y.coerceIn(min.y, max.y), z.coerceIn(min.z, max.z))

    inline fun clampMax(max: Float) = Vector3(min(x, max), min(y, max), min(z, max))

    inline fun clampMin(min: Float) = Vector3(max(x, min), max(y, min), max(z, min))

    inline fun cross(rhs: Vector3): Vector3 {
        val x1 = (y * rhs.z) - (z * rhs.y)
        val y1 = (z * rhs.x) - (x * rhs.z)
        val z1 = (x * rhs.y) - (y * rhs.x)
        return Vector3(x1, y1, z1)
    }

    inline fun distance(rhs: Vector3) = sqrt(distanceSquared(rhs))

    inline fun distanceSquared(rhs: Vector3) = sqr(x - rhs.x) + sqr(y - rhs.y) + sqr(z - rhs.z)

    inline fun dot(x: Float, y: Float, z: Float) = (this.x * x) + (this.y * y) + (this.z * z)

    inline fun dot(vec: Vector3) = dot(vec.x, vec.y, vec.z)

    inline fun equals(rhs: Vector3, epsilon: Float = EPSILON): Boolean {
        return Scalar.equals(x, rhs.x, epsilon) && Scalar.equals(y, rhs.y, epsilon) && Scalar.equals(z, rhs.z, epsilon)
    }

    inline fun lerp(rhs: Vector3, alpha: Float): Vector3 {
        val t = 1.0F - alpha
        val x1 = (x * t) + (rhs.x * alpha)
        val y1 = (y * t) + (rhs.y * alpha)
        val z1 = (z * t) + (rhs.z * alpha)
        return Vector3(x1, y1, z1)
    }

    inline fun min(x: Float, y: Float, z: Float) = Vector3(min(this.x, x), min(this.y, y), min(this.z, z))

    inline fun min(rhs: Vector3) = min(rhs.x, rhs.y, rhs.z)

    inline fun max(x: Float, y: Float, z: Float) = Vector3(max(this.x, x), max(this.y, y), max(this.z, z))

    inline fun max(rhs: Vector3) = max(rhs.x, rhs.y, rhs.z)

    inline fun normalize() = this * Scalar.invSqrt(lengthSquared)

    inline fun reflect(normal: Vector3): Vector3 {
        val d = 2.0F * (lengthSquared)
        val x1 = x - (normal.x * d)
        val y1 = y - (normal.y * d)
        val z1 = z - (normal.z * d)
        return Vector3(x1, y1, z1)
    }

    inline fun squareRoot() = Vector3(sqrt(x), sqrt(y), sqrt(z))

    inline fun transformNormal(matrix: Matrix4): Vector3 {
        val x1 = (x * matrix.m00) + (y * matrix.m10) + (z * matrix.m20)
        val y1 = (x * matrix.m01) + (y * matrix.m11) + (z * matrix.m21)
        val z1 = (x * matrix.m02) + (y * matrix.m12) + (z * matrix.m22)
        return Vector3(x1, y1, z1)
    }

    inline fun toArray(dst: FloatArray = FloatArray(3), offset: Int = 0) {
        dst[offset + 0] = x
        dst[offset + 1] = y
        dst[offset + 2] = z
    }

    companion object {

        val ZERO = Vector3(0.0F)

        val ONE = Vector3(1.0F)
        val MINUS_ONE = Vector3(-1.0F)

        val UNIT_X = Vector3(1.0F, 0.0F, 0.0F)
        val UNIT_Y = Vector3(0.0F, 1.0F, 0.0F)
        val UNIT_Z = Vector3(0.0F, 0.0F, 1.0F)

        val MINUS_UNIT_X = Vector3(-1.0F, 0.0F, 0.0F)
        val MINUS_UNIT_Y = Vector3(0.0F, -1.0F, 0.0F)
        val MINUS_UNIT_Z = Vector3(0.0F, 0.0F, -1.0F)

        val POSITIVE_INFINITY = Vector3(Float.POSITIVE_INFINITY)
        val NEGATIVE_INFINITY = Vector3(Float.NEGATIVE_INFINITY)
    }
}
