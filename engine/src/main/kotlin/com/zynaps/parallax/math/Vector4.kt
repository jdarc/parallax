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

data class Vector4(val x: Float, val y: Float, val z: Float, val w: Float) {

    constructor(x: Number, y: Number, z: Number, w: Number) : this(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())

    constructor(value: Number) : this(value.toFloat(), value.toFloat(), value.toFloat(), value.toFloat())

    constructor(value: Vector2, z: Float, w: Float) : this(value.x, value.y, z, w)

    constructor(value: Vector3, w: Float) : this(value.x, value.y, value.z, w)

    constructor(src: FloatArray, offset: Int = 0) : this(src[offset + 0], src[offset + 1], src[offset + 2], src[offset + 3])

    val length get() = sqrt(lengthSquared)

    val lengthSquared get() = (x * x) + (y * y) + (z * z) + (w * w)

    operator fun unaryMinus() = Vector4(-x, -y, -z, -w)

    operator fun plus(rhs: Float) = Vector4(x + rhs, y + rhs, z + rhs, w + rhs)

    operator fun plus(rhs: Vector4) = Vector4(x + rhs.x, y + rhs.y, z + rhs.z, w + rhs.w)

    operator fun minus(rhs: Float) = Vector4(x - rhs, y - rhs, z - rhs, w - rhs)

    operator fun minus(rhs: Vector4) = Vector4(x - rhs.x, y - rhs.y, z - rhs.z, w - rhs.w)

    operator fun times(rhs: Float) = Vector4(x * rhs, y * rhs, z * rhs, w * rhs)

    operator fun times(rhs: Vector4) = Vector4(x * rhs.x, y * rhs.y, z * rhs.z, w * rhs.w)

    operator fun times(matrix: Matrix4): Vector4 {
        val tx = (x * matrix.m00) + (y * matrix.m10) + (z * matrix.m20) + (w * matrix.m30)
        val ty = (x * matrix.m01) + (y * matrix.m11) + (z * matrix.m21) + (w * matrix.m31)
        val tz = (x * matrix.m02) + (y * matrix.m12) + (z * matrix.m22) + (w * matrix.m32)
        val tw = (x * matrix.m03) + (y * matrix.m13) + (z * matrix.m23) + (w * matrix.m33)
        return Vector4(tx, ty, tz, tw)
    }

    operator fun times(rotation: Quaternion): Vector4 {
        val rx = rotation.x + rotation.x
        val ry = rotation.y + rotation.y
        val rz = rotation.z + rotation.z
        val wx = rotation.w * rx
        val wy = rotation.w * ry
        val wz = rotation.w * rz
        val xx = rotation.x * rx
        val xy = rotation.x * ry
        val xz = rotation.x * rz
        val yy = rotation.y * ry
        val yz = rotation.y * rz
        val zz = rotation.z * rz
        val vx = x * (1F - yy - zz) + y * (xy - wz) + z * (xz + wy)
        val vy = x * (xy + wz) + y * (1F - xx - zz) + z * (yz - wx)
        val vz = x * (xz - wy) + y * (yz + wx) + z * (1F - xx - yy)
        return Vector4(vx, vy, vz, w)
    }

    operator fun div(rhs: Float) = Vector4(x / rhs, y / rhs, z / rhs, w / rhs)

    operator fun div(rhs: Vector4) = Vector4(x / rhs.x, y / rhs.y, z / rhs.z, w / rhs.w)

    operator fun get(index: Int) = when (index) {
        0 -> x
        1 -> y
        2 -> z
        3 -> w
        else -> throw IndexOutOfBoundsException()
    }

    fun abs() = Vector4(abs(x), abs(y), abs(z), abs(w))

    fun angle(rhs: Vector4) = acos((dot(rhs) / (length * rhs.length)).coerceIn(-1F, 1F))

    fun clamp(min: Float, max: Float): Vector4 {
        val x1 = x.coerceIn(min, max)
        val y1 = y.coerceIn(min, max)
        val z1 = z.coerceIn(min, max)
        val w1 = w.coerceIn(min, max)
        return Vector4(x1, y1, z1, w1)
    }

    fun clamp(min: Vector4, max: Vector4): Vector4 {
        val x1 = x.coerceIn(min.x, max.x)
        val y1 = y.coerceIn(min.y, max.y)
        val z1 = z.coerceIn(min.z, max.z)
        val w1 = w.coerceIn(min.w, max.w)
        return Vector4(x1, y1, z1, w1)
    }

    fun clampMax(max: Float) = Vector4(min(x, max), min(y, max), min(z, max), min(w, max))

    fun clampMin(min: Float) = Vector4(max(x, min), max(y, min), max(z, min), max(w, min))

    fun distance(rhs: Vector4) = sqrt(distanceSquared(rhs))

    fun distanceSquared(rhs: Vector4) = sqr(x - rhs.x) + sqr(y - rhs.y) + sqr(z - rhs.z) + sqr(w - rhs.w)

    fun dot(rhs: Vector4) = (x * rhs.x) + (y * rhs.y) + (z * rhs.z) + (w * rhs.w)

    fun equals(rhs: Vector4, epsilon: Float = EPSILON): Boolean {
        return Scalar.equals(x, rhs.x, epsilon) && Scalar.equals(y, rhs.y, epsilon) &&
               Scalar.equals(z, rhs.z, epsilon) && Scalar.equals(w, rhs.w, epsilon)
    }

    fun lerp(rhs: Vector4, alpha: Float): Vector4 {
        val t = 1F - alpha
        val x1 = (x * t) + (rhs.x * alpha)
        val y1 = (y * t) + (rhs.y * alpha)
        val z1 = (z * t) + (rhs.z * alpha)
        val w1 = (w * t) + (rhs.w * alpha)
        return Vector4(x1, y1, z1, w1)
    }

    fun max(x: Float, y: Float, z: Float, w: Float) = Vector4(max(this.x, x), max(this.y, y), max(this.z, z), max(this.w, w))

    fun max(rhs: Vector4) = max(rhs.x, rhs.y, rhs.z, rhs.w)

    fun min(x: Float, y: Float, z: Float, w: Float) = Vector4(min(this.x, x), min(this.y, y), min(this.z, z), min(this.w, w))

    fun min(rhs: Vector4) = min(rhs.x, rhs.y, rhs.z, rhs.w)

    fun normalize(vector: Vector4) = vector * (1F / vector.length)

    fun project() = this * (1F / w)

    fun squareRoot(vector: Vector4) = Vector4(
        sqrt(vector.x),
        sqrt(vector.y),
        sqrt(vector.z),
        sqrt(vector.w)
    )

    fun toArray(dst: FloatArray = FloatArray(4), offset: Int = 0) {
        dst[offset + 0] = x
        dst[offset + 1] = y
        dst[offset + 2] = z
        dst[offset + 3] = w
    }

    companion object {
        val ZERO = Vector4(0)

        val ONE = Vector4(1)
        val MINUS_ONE = Vector4(-1)

        val UNIT_X = Vector4(1, 0, 0, 0)
        val UNIT_Y = Vector4(0, 1, 0, 0)
        val UNIT_Z = Vector4(0, 0, 1, 0)
        val UNIT_W = Vector4(0, 0, 0, 1)

        val MINUS_UNIT_X = Vector4(-1, 0, 0, 0)
        val MINUS_UNIT_Y = Vector4(0, -1, 0, 0)
        val MINUS_UNIT_Z = Vector4(0, 0, -1, 0)
        val MINUS_UNIT_W = Vector4(0, 0, 0, -1)

        val POSITIVE_INFINITY = Vector4(Float.POSITIVE_INFINITY)
        val NEGATIVE_INFINITY = Vector4(Float.NEGATIVE_INFINITY)
    }
}
