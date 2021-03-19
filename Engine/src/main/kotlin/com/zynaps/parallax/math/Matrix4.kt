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
import com.zynaps.parallax.math.Scalar.HALF_PI
import com.zynaps.parallax.math.Scalar.abs
import com.zynaps.parallax.math.Scalar.cos
import com.zynaps.parallax.math.Scalar.equals
import com.zynaps.parallax.math.Scalar.sin
import com.zynaps.parallax.math.Scalar.sqrt
import com.zynaps.parallax.math.Scalar.tan

@Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode", "SpellCheckingInspection", "NOTHING_TO_INLINE")
data class Matrix4(
    inline val m00: Float, inline val m10: Float, inline val m20: Float, inline val m30: Float,
    inline val m01: Float, inline val m11: Float, inline val m21: Float, inline val m31: Float,
    inline val m02: Float, inline val m12: Float, inline val m22: Float, inline val m32: Float,
    inline val m03: Float, inline val m13: Float, inline val m23: Float, inline val m33: Float
) {

    constructor(v: Float) : this(v, v, v, v, v, v, v, v, v, v, v, v, v, v, v, v)

    constructor(src: FloatArray, offset: Int = 0) : this(
        src[offset + 0x0], src[offset + 0x1], src[offset + 0x2], src[offset + 0x3],
        src[offset + 0x4], src[offset + 0x5], src[offset + 0x6], src[offset + 0x7],
        src[offset + 0x8], src[offset + 0x9], src[offset + 0xA], src[offset + 0xB],
        src[offset + 0xC], src[offset + 0xD], src[offset + 0xE], src[offset + 0xF]
    )

    inline val isIdentity get() = equals(IDENTITY)

    inline val determinant: Float
        get() {
            val d0 = m00 * (m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32 - m13 * m22 * m31 - m11 * m23 * m32 - m12 * m21 * m33)
            val d1 = m01 * (m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32 - m13 * m22 * m30 - m10 * m23 * m32 - m12 * m20 * m33)
            val d2 = m02 * (m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31 - m13 * m21 * m30 - m10 * m23 * m31 - m11 * m20 * m33)
            val d3 = m03 * (m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31 - m12 * m21 * m30 - m10 * m22 * m31 - m11 * m20 * m32)
            return d0 - d1 + d2 - d3
        }

    inline operator fun unaryMinus() = Matrix4(
        -m00, -m10, -m20, -m30,
        -m01, -m11, -m21, -m31,
        -m02, -m12, -m22, -m32,
        -m03, -m13, -m23, -m33
    )

    inline operator fun plus(rhs: Float) = Matrix4(
        m00 + rhs, m10 + rhs, m20 + rhs, m30 + rhs,
        m01 + rhs, m11 + rhs, m21 + rhs, m31 + rhs,
        m02 + rhs, m12 + rhs, m22 + rhs, m32 + rhs,
        m03 + rhs, m13 + rhs, m23 + rhs, m33 + rhs
    )

    inline operator fun plus(rhs: Matrix4) = Matrix4(
        m00 + rhs.m00, m10 + rhs.m10, m20 + rhs.m20, m30 + rhs.m30,
        m01 + rhs.m01, m11 + rhs.m11, m21 + rhs.m21, m31 + rhs.m31,
        m02 + rhs.m02, m12 + rhs.m12, m22 + rhs.m22, m32 + rhs.m32,
        m03 + rhs.m03, m13 + rhs.m13, m23 + rhs.m23, m33 + rhs.m33
    )

    inline operator fun minus(rhs: Float) = Matrix4(
        m00 - rhs, m10 - rhs, m20 - rhs, m30 - rhs,
        m01 - rhs, m11 - rhs, m21 - rhs, m31 - rhs,
        m02 - rhs, m12 - rhs, m22 - rhs, m32 - rhs,
        m03 - rhs, m13 - rhs, m23 - rhs, m33 - rhs
    )

    inline operator fun minus(rhs: Matrix4) = Matrix4(
        m00 - rhs.m00, m10 - rhs.m10, m20 - rhs.m20, m30 - rhs.m30,
        m01 - rhs.m01, m11 - rhs.m11, m21 - rhs.m21, m31 - rhs.m31,
        m02 - rhs.m02, m12 - rhs.m12, m22 - rhs.m22, m32 - rhs.m32,
        m03 - rhs.m03, m13 - rhs.m13, m23 - rhs.m23, m33 - rhs.m33
    )

    inline operator fun times(rhs: Float) = Matrix4(
        m00 * rhs, m10 * rhs, m20 * rhs, m30 * rhs,
        m01 * rhs, m11 * rhs, m21 * rhs, m31 * rhs,
        m02 * rhs, m12 * rhs, m22 * rhs, m32 * rhs,
        m03 * rhs, m13 * rhs, m23 * rhs, m33 * rhs
    )

    inline operator fun times(rhs: Vector3) = Vector3(
        (m00 * rhs.x) + (m01 * rhs.y) + (m02 * rhs.z) + m03,
        (m10 * rhs.x) + (m11 * rhs.y) + (m12 * rhs.z) + m13,
        (m20 * rhs.x) + (m21 * rhs.y) + (m22 * rhs.z) + m23
    )

    inline operator fun times(rhs: Vector4) = Vector4(
        (m00 * rhs.x) + (m01 * rhs.y) + (m02 * rhs.z) + (m03 * rhs.w),
        (m10 * rhs.x) + (m11 * rhs.y) + (m12 * rhs.z) + (m13 * rhs.w),
        (m20 * rhs.x) + (m21 * rhs.y) + (m22 * rhs.z) + (m23 * rhs.w),
        (m30 * rhs.x) + (m31 * rhs.y) + (m32 * rhs.z) + (m33 * rhs.w)
    )

    inline operator fun times(rhs: Matrix4) = Matrix4(
        (m00 * rhs.m00) + (m01 * rhs.m10) + (m02 * rhs.m20) + (m03 * rhs.m30),
        (m10 * rhs.m00) + (m11 * rhs.m10) + (m12 * rhs.m20) + (m13 * rhs.m30),
        (m20 * rhs.m00) + (m21 * rhs.m10) + (m22 * rhs.m20) + (m23 * rhs.m30),
        (m30 * rhs.m00) + (m31 * rhs.m10) + (m32 * rhs.m20) + (m33 * rhs.m30),
        (m00 * rhs.m01) + (m01 * rhs.m11) + (m02 * rhs.m21) + (m03 * rhs.m31),
        (m10 * rhs.m01) + (m11 * rhs.m11) + (m12 * rhs.m21) + (m13 * rhs.m31),
        (m20 * rhs.m01) + (m21 * rhs.m11) + (m22 * rhs.m21) + (m23 * rhs.m31),
        (m30 * rhs.m01) + (m31 * rhs.m11) + (m32 * rhs.m21) + (m33 * rhs.m31),
        (m00 * rhs.m02) + (m01 * rhs.m12) + (m02 * rhs.m22) + (m03 * rhs.m32),
        (m10 * rhs.m02) + (m11 * rhs.m12) + (m12 * rhs.m22) + (m13 * rhs.m32),
        (m20 * rhs.m02) + (m21 * rhs.m12) + (m22 * rhs.m22) + (m23 * rhs.m32),
        (m30 * rhs.m02) + (m31 * rhs.m12) + (m32 * rhs.m22) + (m33 * rhs.m32),
        (m00 * rhs.m03) + (m01 * rhs.m13) + (m02 * rhs.m23) + (m03 * rhs.m33),
        (m10 * rhs.m03) + (m11 * rhs.m13) + (m12 * rhs.m23) + (m13 * rhs.m33),
        (m20 * rhs.m03) + (m21 * rhs.m13) + (m22 * rhs.m23) + (m23 * rhs.m33),
        (m30 * rhs.m03) + (m31 * rhs.m13) + (m32 * rhs.m23) + (m33 * rhs.m33)
    )

    inline operator fun get(row: Int, col: Int) = when (row) {
        0 -> when (col) { 0 -> m00; 1 -> m01; 2 -> m02; 3 -> m03; else -> throw IndexOutOfBoundsException() }
        1 -> when (col) { 0 -> m10; 1 -> m11; 2 -> m12; 3 -> m13; else -> throw IndexOutOfBoundsException() }
        2 -> when (col) { 0 -> m20; 1 -> m21; 2 -> m22; 3 -> m23; else -> throw IndexOutOfBoundsException() }
        3 -> when (col) { 0 -> m30; 1 -> m31; 2 -> m32; 3 -> m33; else -> throw IndexOutOfBoundsException() }
        else -> throw IndexOutOfBoundsException()
    }

    inline fun getRow(row: Int) = when (row) {
        0 -> Vector4(m00, m01, m02, m03)
        1 -> Vector4(m10, m11, m12, m13)
        2 -> Vector4(m20, m21, m22, m23)
        3 -> Vector4(m30, m31, m32, m33)
        else -> throw IndexOutOfBoundsException()
    }

    inline fun getColumn(col: Int) = when (col) {
        0 -> Vector4(m00, m10, m20, m30)
        1 -> Vector4(m01, m11, m21, m31)
        2 -> Vector4(m02, m12, m22, m32)
        3 -> Vector4(m03, m13, m23, m33)
        else -> throw IndexOutOfBoundsException()
    }

    inline fun toArray(dst: FloatArray, offset: Int = 0) {
        dst[offset + 0x0] = m00; dst[offset + 0x1] = m10; dst[offset + 0x2] = m20; dst[offset + 0x3] = m30
        dst[offset + 0x4] = m01; dst[offset + 0x5] = m11; dst[offset + 0x6] = m21; dst[offset + 0x7] = m31
        dst[offset + 0x8] = m02; dst[offset + 0x9] = m12; dst[offset + 0xA] = m22; dst[offset + 0xB] = m32
        dst[offset + 0xC] = m03; dst[offset + 0xD] = m13; dst[offset + 0xE] = m23; dst[offset + 0xF] = m33
    }

    inline fun equals(rhs: Matrix4, epsilon: Float = EPSILON): Boolean {
        return equals(m00, rhs.m00, epsilon) && equals(m10, rhs.m10, epsilon) &&
               equals(m20, rhs.m20, epsilon) && equals(m30, rhs.m30, epsilon) &&
               equals(m01, rhs.m01, epsilon) && equals(m11, rhs.m11, epsilon) &&
               equals(m21, rhs.m21, epsilon) && equals(m31, rhs.m31, epsilon) &&
               equals(m02, rhs.m02, epsilon) && equals(m12, rhs.m12, epsilon) &&
               equals(m22, rhs.m22, epsilon) && equals(m32, rhs.m32, epsilon) &&
               equals(m03, rhs.m03, epsilon) && equals(m13, rhs.m13, epsilon) &&
               equals(m23, rhs.m23, epsilon) && equals(m33, rhs.m33, epsilon)
    }

    inline val invert: Matrix4
        get() {
            val b00 = m00 * m11 - m10 * m01
            val b01 = m00 * m21 - m20 * m01
            val b02 = m00 * m31 - m30 * m01
            val b03 = m10 * m21 - m20 * m11
            val b04 = m10 * m31 - m30 * m11
            val b05 = m20 * m31 - m30 * m21
            val b06 = m02 * m13 - m12 * m03
            val b07 = m02 * m23 - m22 * m03
            val b08 = m02 * m33 - m32 * m03
            val b09 = m12 * m23 - m22 * m13
            val b10 = m12 * m33 - m32 * m13
            val b11 = m22 * m33 - m32 * m23
            val det = b00 * b11 - b01 * b10 + b02 * b09 + b03 * b08 - b04 * b07 + b05 * b06
            if (abs(det) < EPSILON) return ERROR
            val invDet = 1.0F / det
            val a = (b11 * m11 - b10 * m21 + b09 * m31) * invDet
            val b = (b10 * m20 - b11 * m10 - b09 * m30) * invDet
            val c = (b05 * m13 - b04 * m23 + b03 * m33) * invDet
            val d = (b04 * m22 - b05 * m12 - b03 * m32) * invDet
            val e = (b08 * m21 - b11 * m01 - b07 * m31) * invDet
            val f = (b11 * m00 - b08 * m20 + b07 * m30) * invDet
            val g = (b02 * m23 - b05 * m03 - b01 * m33) * invDet
            val h = (b05 * m02 - b02 * m22 + b01 * m32) * invDet
            val i = (b10 * m01 - b08 * m11 + b06 * m31) * invDet
            val j = (b08 * m10 - b10 * m00 - b06 * m30) * invDet
            val k = (b04 * m03 - b02 * m13 + b00 * m33) * invDet
            val l = (b02 * m12 - b04 * m02 - b00 * m32) * invDet
            val m = (b07 * m11 - b09 * m01 - b06 * m21) * invDet
            val n = (b09 * m00 - b07 * m10 + b06 * m20) * invDet
            val o = (b01 * m13 - b03 * m03 - b00 * m23) * invDet
            val p = (b03 * m02 - b01 * m12 + b00 * m22) * invDet
            return Matrix4(a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p)
        }

    inline fun transpose() = Matrix4(
        m00, m01, m02, m03,
        m10, m11, m12, m13,
        m20, m21, m22, m23,
        m30, m31, m32, m33
    )

    inline fun transformNormal(rhs: Vector3) = Vector3(
        (m00 * rhs.x) + (m01 * rhs.y) + (m02 * rhs.z),
        (m10 * rhs.x) + (m11 * rhs.y) + (m12 * rhs.z),
        (m20 * rhs.x) + (m21 * rhs.y) + (m22 * rhs.z)
    )

    companion object {

        val ZERO = Matrix4(0.0F)

        val IDENTITY = Matrix4(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)

        val ERROR = Matrix4(Float.NaN)

        fun createTranslation(x: Float, y: Float, z: Float) =
            Matrix4(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, x, y, z, 1.0F)

        fun createTranslation(v: Vector3) = createTranslation(v.x, v.y, v.z)

        fun createScale(x: Float, y: Float, z: Float) =
            Matrix4(x, 0.0F, 0.0F, 0.0F, 0.0F, y, 0.0F, 0.0F, 0.0F, 0.0F, z, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)

        fun createScale(scales: Vector3) = createScale(scales.x, scales.y, scales.z)

        fun createScale(scale: Float) = createScale(scale, scale, scale)

        fun createRotationX(angle: Float): Matrix4 {
            val cos = cos(angle)
            val sin = sin(angle)
            return Matrix4(1.0F, 0.0F, 0.0F, 0.0F, 0.0F, cos, sin, 0.0F, 0.0F, -sin, cos, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)
        }

        fun createRotationY(angle: Float): Matrix4 {
            val cos = cos(angle)
            val sin = sin(angle)
            return Matrix4(cos, 0.0F, -sin, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, sin, 0.0F, cos, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)
        }

        fun createRotationZ(angle: Float): Matrix4 {
            val cos = cos(angle)
            val sin = sin(angle)
            return Matrix4(cos, sin, 0.0F, 0.0F, -sin, cos, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)
        }

        fun createFromMatrix3x3(m: Matrix3) =
            Matrix4(m.m00, m.m10, m.m20, 0.0F, m.m01, m.m11, m.m21, 0.0F, m.m02, m.m12, m.m22, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)

        fun createFromAxisAngle(x: Float, y: Float, z: Float, angle: Float): Matrix4 {
            val magnitude = sqrt(x * x + y * y + z * z)
            if (magnitude < EPSILON) return ERROR
            val ax = x / magnitude
            val ay = y / magnitude
            val az = z / magnitude
            val cos = cos(angle)
            val sin = sin(angle)
            val t = 1.0F - cos
            val xz = ax * az
            val xy = ax * ay
            val yz = ay * az
            val m00 = t * ax * ax + cos
            val m01 = t * xy - az * sin
            val m02 = t * xz + ay * sin
            val m10 = t * xy + az * sin
            val m11 = t * ay * ay + cos
            val m12 = t * yz - ax * sin
            val m20 = t * xz - ay * sin
            val m21 = t * yz + ax * sin
            val m22 = t * az * az + cos
            return Matrix4(m00, m10, m20, 0.0F, m01, m11, m21, 0.0F, m02, m12, m22, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)
        }

        fun createFromAxisAngle(axis: Vector3, angle: Float) = createFromAxisAngle(axis.x, axis.y, axis.z, angle)

        fun createFromYawPitchRoll(yaw: Float, pitch: Float, roll: Float): Matrix4 {
            val sr = sin(roll * 0.5F)
            val cr = cos(roll * 0.5F)
            val sp = sin(pitch * 0.5F)
            val cp = cos(pitch * 0.5F)
            val sy = sin(yaw * 0.5F)
            val cy = cos(yaw * 0.5F)
            val qx = cy * sp * cr + sy * cp * sr
            val qy = sy * cp * cr - cy * sp * sr
            val qz = cy * cp * sr - sy * sp * cr
            val qw = cy * cp * cr + sy * sp * sr
            val m00 = 1.0F - 2.0F * qy * qy - 2.0F * qz * qz
            val m01 = 2.0F * (qx * qy - qw * qz)
            val m02 = 2.0F * (qx * qz + qw * qy)
            val m10 = 2.0F * (qx * qy + qw * qz)
            val m11 = 1.0F - 2.0F * qx * qx - 2.0F * qz * qz
            val m12 = 2.0F * (qy * qz - qw * qx)
            val m20 = 2.0F * (qx * qz - qw * qy)
            val m21 = 2.0F * (qy * qz + qw * qx)
            val m22 = 1.0F - 2.0F * qx * qx - 2.0F * qy * qy
            return Matrix4(m00, m10, m20, 0.0F, m01, m11, m21, 0.0F, m02, m12, m22, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)
        }

        fun createFromQuaternion(q: Quaternion): Matrix4 {
            val lenSqr = q.lengthSquared
            if (lenSqr < EPSILON) return ERROR
            val invLen = 1.0F / sqrt(lenSqr)
            val nx = q.x * invLen
            val ny = q.y * invLen
            val nz = q.z * invLen
            val nw = q.w * invLen
            val m00 = 1.0F - 2.0F * ny * ny - 2.0F * nz * nz
            val m01 = 2.0F * nx * ny - 2.0F * nz * nw
            val m02 = 2.0F * nx * nz + 2.0F * ny * nw
            val m10 = 2.0F * nx * ny + 2.0F * nz * nw
            val m11 = 1.0F - 2.0F * nx * nx - 2.0F * nz * nz
            val m12 = 2.0F * ny * nz - 2.0F * nx * nw
            val m20 = 2.0F * nx * nz - 2.0F * ny * nw
            val m21 = 2.0F * ny * nz + 2.0F * nx * nw
            val m22 = 1.0F - 2.0F * nx * nx - 2.0F * ny * ny
            return Matrix4(m00, m10, m20, 0.0F, m01, m11, m21, 0.0F, m02, m12, m22, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)
        }

        fun createWorld(pos: Vector3, fwd: Vector3, up: Vector3): Matrix4 {
            val d = (-fwd).normalize()
            val r = up.cross(d).normalize()
            val u = d.cross(r)
            return Matrix4(r.x, r.y, r.z, 0.0F, u.x, u.y, u.z, 0.0F, d.x, d.y, d.z, 0.0F, pos.x, pos.y, pos.z, 1.0F)
        }

        fun createFromPositionRotationScale(position: Vector3, rotation: Matrix4, scale: Vector3): Matrix4 {
            val m00 = rotation.m00 * scale.x
            val m01 = rotation.m01 * scale.y
            val m02 = rotation.m02 * scale.z
            val m03 = position.x
            val m10 = rotation.m10 * scale.x
            val m11 = rotation.m11 * scale.y
            val m12 = rotation.m12 * scale.z
            val m13 = position.y
            val m20 = rotation.m20 * scale.x
            val m21 = rotation.m21 * scale.y
            val m22 = rotation.m22 * scale.z
            val m23 = position.z
            return Matrix4(m00, m10, m20, 0.0F, m01, m11, m21, 0.0F, m02, m12, m22, 0.0F, m03, m13, m23, 1.0F)
        }

        fun createNormalTransform(matrix: Matrix4): Matrix4 {
            val b00 = matrix.m00 * matrix.m11 - matrix.m10 * matrix.m01
            val b01 = matrix.m00 * matrix.m21 - matrix.m20 * matrix.m01
            val b02 = matrix.m00 * matrix.m31 - matrix.m30 * matrix.m01
            val b03 = matrix.m10 * matrix.m21 - matrix.m20 * matrix.m11
            val b04 = matrix.m10 * matrix.m31 - matrix.m30 * matrix.m11
            val b05 = matrix.m20 * matrix.m31 - matrix.m30 * matrix.m21
            val b06 = matrix.m02 * matrix.m13 - matrix.m12 * matrix.m03
            val b07 = matrix.m02 * matrix.m23 - matrix.m22 * matrix.m03
            val b08 = matrix.m02 * matrix.m33 - matrix.m32 * matrix.m03
            val b09 = matrix.m12 * matrix.m23 - matrix.m22 * matrix.m13
            val b10 = matrix.m12 * matrix.m33 - matrix.m32 * matrix.m13
            val b11 = matrix.m22 * matrix.m33 - matrix.m32 * matrix.m23
            val invDet = 1.0F / (b00 * b11 - b01 * b10 + b02 * b09 + b03 * b08 - b04 * b07 + b05 * b06)
            val t00 = (b11 * matrix.m11 - b10 * matrix.m21 + b09 * matrix.m31) * invDet
            val t01 = (b10 * matrix.m20 - b11 * matrix.m10 - b09 * matrix.m30) * invDet
            val t02 = (b05 * matrix.m13 - b04 * matrix.m23 + b03 * matrix.m33) * invDet
            val t10 = (b08 * matrix.m21 - b11 * matrix.m01 - b07 * matrix.m31) * invDet
            val t11 = (b11 * matrix.m00 - b08 * matrix.m20 + b07 * matrix.m30) * invDet
            val t12 = (b02 * matrix.m23 - b05 * matrix.m03 - b01 * matrix.m33) * invDet
            val t20 = (b10 * matrix.m01 - b08 * matrix.m11 + b06 * matrix.m31) * invDet
            val t21 = (b08 * matrix.m10 - b10 * matrix.m00 - b06 * matrix.m30) * invDet
            val t22 = (b04 * matrix.m03 - b02 * matrix.m13 + b00 * matrix.m33) * invDet
            val row0 = Scalar.invSqrt(t00 * t00 + t10 * t10 + t20 * t20)
            val row1 = Scalar.invSqrt(t01 * t01 + t11 * t11 + t21 * t21)
            val row2 = Scalar.invSqrt(t02 * t02 + t12 * t12 + t22 * t22)
            val m00 = t00 * row0
            val m10 = t10 * row0
            val m20 = t20 * row0
            val m01 = t01 * row1
            val m11 = t11 * row1
            val m21 = t21 * row1
            val m02 = t02 * row2
            val m12 = t12 * row2
            val m22 = t22 * row2
            return Matrix4(m00, m10, m20, 0.0F, m01, m11, m21, 0.0F, m02, m12, m22, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F)
        }

        fun createLookAt(eye: Vector3, at: Vector3, up: Vector3): Matrix4 {
            val d = (eye - at).normalize()
            val r = up.cross(d).normalize()
            val u = d.cross(r)
            val x = -r.dot(eye)
            val y = -u.dot(eye)
            val z = -d.dot(eye)
            return Matrix4(r.x, u.x, d.x, 0.0F, r.y, u.y, d.y, 0.0F, r.z, u.z, d.z, 0.0F, x, y, z, 1.0F)
        }

        fun createOrthographic(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float): Matrix4 {
            if (left >= right) throw IllegalArgumentException("left >= right")
            if (bottom >= top) throw IllegalArgumentException("bottom >= top")
            if (near <= 0.0F) throw IllegalArgumentException("near < 0")
            if (far <= 0.0F) throw IllegalArgumentException("far <= 0")
            if (near >= far) throw IllegalArgumentException("near >= far")
            val m00 = 2.0F / (right - left)
            val m11 = 2.0F / (top - bottom)
            val m22 = -2.0F / (far - near)
            val m03 = -((right + left) / (right - left))
            val m13 = -((top + bottom) / (top - bottom))
            val m23 = -((far + near) / (far - near))
            return Matrix4(m00, 0.0F, 0.0F, 0.0F, 0.0F, m11, 0.0F, 0.0F, 0.0F, 0.0F, m22, 0.0F, m03, m13, m23, 1.0F)
        }

        fun createPerspectiveFov(fov: Float, aspectRatio: Float, near: Float, far: Float): Matrix4 {
            if (fov <= 0.0F) throw IllegalArgumentException("fov <= 0")
            if (fov >= Math.PI) throw IllegalArgumentException("fov >= PI")
            if (near <= 0.0F) throw IllegalArgumentException("near < 0")
            if (far <= 0.0F) throw IllegalArgumentException("far <= 0")
            if (near >= far) throw IllegalArgumentException("near >= far")
            val rangeInv = 1.0F / (near - far)
            val m11 = tan(HALF_PI - 0.5F * fov)
            val m00 = m11 / aspectRatio
            val m22 = (far + near) * rangeInv
            val m23 = 2.0F * far * near * rangeInv
            return Matrix4(m00, 0.0F, 0.0F, 0.0F, 0.0F, m11, 0.0F, 0.0F, 0.0F, 0.0F, m22, -1.0F, 0.0F, 0.0F, m23, 0.0F)
        }

        fun createReflection(plane: Plane): Matrix4 {
            val fa = -2.0F * plane.normal.x
            val fb = -2.0F * plane.normal.y
            val fc = -2.0F * plane.normal.z
            val m00 = fa * plane.normal.x + 1.0F
            val m01 = fa * plane.normal.y
            val m02 = fa * plane.normal.z
            val m03 = fa * plane.distance
            val m10 = fb * plane.normal.x
            val m11 = fb * plane.normal.y + 1.0F
            val m12 = fb * plane.normal.z
            val m13 = fb * plane.distance
            val m20 = fc * plane.normal.x
            val m21 = fc * plane.normal.y
            val m22 = fc * plane.normal.z + 1.0F
            val m23 = fc * plane.distance
            return Matrix4(m00, m10, m20, 0.0F, m01, m11, m21, 0.0F, m02, m12, m22, 0.0F, m03, m13, m23, 1.0F)
        }
    }
}
