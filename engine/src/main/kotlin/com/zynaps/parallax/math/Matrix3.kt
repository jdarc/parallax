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
import com.zynaps.parallax.math.Scalar.cos
import com.zynaps.parallax.math.Scalar.equals
import com.zynaps.parallax.math.Scalar.sin
import com.zynaps.parallax.math.Scalar.sqrt

data class Matrix3(
    val m00: Float, val m10: Float, val m20: Float,
    val m01: Float, val m11: Float, val m21: Float,
    val m02: Float, val m12: Float, val m22: Float
) {

    constructor(v: Float) : this(v, v, v, v, v, v, v, v, v)

    constructor(src: FloatArray, offset: Int = 0) : this(
        src[offset + 0], src[offset + 1], src[offset + 2],
        src[offset + 3], src[offset + 4], src[offset + 5],
        src[offset + 6], src[offset + 7], src[offset + 8]
    )

    val isIdentity get() = equals(IDENTITY)

    val determinant: Float
        get() = m00 * (m11 * m22 - m12 * m21) + m01 * (m12 * m20 - m10 * m22) + m02 * (m10 * m21 - m11 * m20)

    operator fun unaryMinus() = Matrix3(-m00, -m10, -m20, -m01, -m11, -m21, -m02, -m12, -m22)

    operator fun plus(rhs: Float) = Matrix3(
        m00 + rhs, m10 + rhs, m20 + rhs,
        m01 + rhs, m11 + rhs, m21 + rhs,
        m02 + rhs, m12 + rhs, m22 + rhs
    )

    operator fun plus(rhs: Matrix3) = Matrix3(
        m00 + rhs.m00, m10 + rhs.m10, m20 + rhs.m20,
        m01 + rhs.m01, m11 + rhs.m11, m21 + rhs.m21,
        m02 + rhs.m02, m12 + rhs.m12, m22 + rhs.m22
    )

    operator fun minus(rhs: Float) = Matrix3(
        m00 - rhs, m10 - rhs, m20 - rhs,
        m01 - rhs, m11 - rhs, m21 - rhs,
        m02 - rhs, m12 - rhs, m22 - rhs
    )

    operator fun minus(rhs: Matrix3) = Matrix3(
        m00 - rhs.m00, m10 - rhs.m10, m20 - rhs.m20,
        m01 - rhs.m01, m11 - rhs.m11, m21 - rhs.m21,
        m02 - rhs.m02, m12 - rhs.m12, m22 - rhs.m22
    )

    operator fun times(rhs: Float) = Matrix3(
        m00 * rhs, m10 * rhs, m20 * rhs,
        m01 * rhs, m11 * rhs, m21 * rhs,
        m02 * rhs, m12 * rhs, m22 * rhs
    )

    operator fun times(rhs: Vector3) = Vector3(
        (m00 * rhs.x) + (m10 * rhs.y) + (m20 * rhs.z),
        (m01 * rhs.x) + (m11 * rhs.y) + (m21 * rhs.z),
        (m02 * rhs.x) + (m12 * rhs.y) + (m22 * rhs.z)
    )

    operator fun times(rhs: Matrix3) = Matrix3(
        (m00 * rhs.m00) + (m01 * rhs.m10) + (m02 * rhs.m20),
        (m10 * rhs.m00) + (m11 * rhs.m10) + (m12 * rhs.m20),
        (m20 * rhs.m00) + (m21 * rhs.m10) + (m22 * rhs.m20),
        (m00 * rhs.m01) + (m01 * rhs.m11) + (m02 * rhs.m21),
        (m10 * rhs.m01) + (m11 * rhs.m11) + (m12 * rhs.m21),
        (m20 * rhs.m01) + (m21 * rhs.m11) + (m22 * rhs.m21),
        (m00 * rhs.m02) + (m01 * rhs.m12) + (m02 * rhs.m22),
        (m10 * rhs.m02) + (m11 * rhs.m12) + (m12 * rhs.m22),
        (m20 * rhs.m02) + (m21 * rhs.m12) + (m22 * rhs.m22),
    )

    operator fun get(index: Int) = when (index) {
        0 -> m00; 1 -> m10; 2 -> m20
        3 -> m01; 4 -> m11; 5 -> m21
        6 -> m02; 7 -> m12; 8 -> m22
        else -> throw IndexOutOfBoundsException()
    }

    operator fun get(row: Int, col: Int) = when (row) {
        0 -> when (col) {
            0 -> m00; 1 -> m01; 2 -> m02; else -> throw IndexOutOfBoundsException()
        }
        1 -> when (col) {
            0 -> m10; 1 -> m11; 2 -> m12; else -> throw IndexOutOfBoundsException()
        }
        2 -> when (col) {
            0 -> m20; 1 -> m21; 2 -> m22; else -> throw IndexOutOfBoundsException()
        }
        else -> throw IndexOutOfBoundsException()
    }

    fun getRow(row: Int) = when (row) {
        0 -> Vector3(m00, m01, m02)
        1 -> Vector3(m10, m11, m12)
        2 -> Vector3(m20, m21, m22)
        else -> throw IndexOutOfBoundsException()
    }

    fun getColumn(col: Int) = when (col) {
        0 -> Vector3(m00, m10, m20)
        1 -> Vector3(m01, m11, m21)
        2 -> Vector3(m02, m12, m22)
        else -> throw IndexOutOfBoundsException()
    }

    fun toArray(dst: FloatArray, offset: Int = 0) {
        dst[offset + 0x0] = m00; dst[offset + 0x1] = m10; dst[offset + 0x2] = m20
        dst[offset + 0x3] = m01; dst[offset + 0x4] = m11; dst[offset + 0x5] = m21
        dst[offset + 0x6] = m02; dst[offset + 0x7] = m12; dst[offset + 0x8] = m22
    }

    fun equals(rhs: Matrix3, epsilon: Float = EPSILON) =
        equals(m00, rhs.m00, epsilon) && equals(m01, rhs.m01, epsilon) && equals(m02, rhs.m02, epsilon) &&
        equals(m10, rhs.m10, epsilon) && equals(m11, rhs.m11, epsilon) && equals(m12, rhs.m12, epsilon) &&
        equals(m20, rhs.m20, epsilon) && equals(m21, rhs.m21, epsilon) && equals(m22, rhs.m22, epsilon)

    fun invert(): Matrix3 {
        val d0 = m11 * m22 - m12 * m21
        val d1 = m12 * m20 - m10 * m22
        val d2 = m10 * m21 - m11 * m20
        val det = m00 * d0 + m01 * d1 + m02 * d2
        if (abs(det) < EPSILON) return ERROR
        val invDet = 1F / det
        val t00 = d0 * invDet
        val t01 = (m02 * m21 - m01 * m22) * invDet
        val t02 = (m01 * m12 - m02 * m11) * invDet
        val t10 = d1 * invDet
        val t11 = (m00 * m22 - m02 * m20) * invDet
        val t12 = (m02 * m10 - m00 * m12) * invDet
        val t20 = d2 * invDet
        val t21 = (m01 * m20 - m00 * m21) * invDet
        val t22 = (m00 * m11 - m01 * m10) * invDet
        return Matrix3(t00, t01, t02, t10, t11, t12, t20, t21, t22)
    }

    fun transpose() = Matrix3(m00, m01, m02, m10, m11, m12, m20, m21, m22)

    companion object {

        val ZERO = Matrix3(0F)

        val IDENTITY = Matrix3(1F, 0F, 0F, 0F, 1F, 0F, 0F, 0F, 1F)

        val ERROR = Matrix3(Float.NaN)

        fun createScale(x: Float, y: Float, z: Float) = Matrix3(x, 0F, 0F, 0F, y, 0F, 0F, 0F, z)

        fun createScale(scales: Vector3) = createScale(scales.x, scales.y, scales.z)

        fun createScale(scale: Float) = createScale(scale, scale, scale)

        fun createRotationX(angle: Float): Matrix3 {
            val cos = cos(angle)
            val sin = sin(angle)
            return Matrix3(1F, 0F, 0F, 0F, cos, sin, 0F, -sin, cos)
        }

        fun createRotationY(angle: Float): Matrix3 {
            val cos = cos(angle)
            val sin = sin(angle)
            return Matrix3(cos, 0F, -sin, 0F, 1F, 0F, sin, 0F, cos)
        }

        fun createRotationZ(angle: Float): Matrix3 {
            val cos = cos(angle)
            val sin = sin(angle)
            return Matrix3(cos, sin, 0F, -sin, cos, 0F, 0F, 0F, 1F)
        }

        fun createFromAxisAngle(x: Float, y: Float, z: Float, angle: Float): Matrix3 {
            val magnitude = sqrt(x * x + y * y + z * z)
            if (magnitude < EPSILON) return ERROR
            val ax = x / magnitude
            val ay = y / magnitude
            val az = z / magnitude
            val cos = cos(angle)
            val sin = sin(angle)
            val t = 1F - cos
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
            return Matrix3(m00, m10, m20, m01, m11, m21, m02, m12, m22)
        }

        fun createFromAxisAngle(axis: Vector3, angle: Float) = createFromAxisAngle(axis.x, axis.y, axis.z, angle)

        fun createFromYawPitchRoll(yaw: Float, pitch: Float, roll: Float): Matrix3 {
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
            val m00 = 1F - 2F * qy * qy - 2F * qz * qz
            val m01 = 2F * (qx * qy - qw * qz)
            val m02 = 2F * (qx * qz + qw * qy)
            val m10 = 2F * (qx * qy + qw * qz)
            val m11 = 1F - 2F * qx * qx - 2F * qz * qz
            val m12 = 2F * (qy * qz - qw * qx)
            val m20 = 2F * (qx * qz - qw * qy)
            val m21 = 2F * (qy * qz + qw * qx)
            val m22 = 1F - 2F * qx * qx - 2F * qy * qy
            return Matrix3(m00, m10, m20, m01, m11, m21, m02, m12, m22)
        }

        fun createFromQuaternion(q: Quaternion): Matrix3 {
            val lenSqr = q.lengthSquared
            if (lenSqr < EPSILON) return ERROR
            val invLen = 1F / sqrt(lenSqr)
            val nx = q.x * invLen
            val ny = q.y * invLen
            val nz = q.z * invLen
            val nw = q.w * invLen
            val m00 = 1F - 2F * ny * ny - 2F * nz * nz
            val m01 = 2F * nx * ny - 2F * nz * nw
            val m02 = 2F * nx * nz + 2F * ny * nw
            val m10 = 2F * nx * ny + 2F * nz * nw
            val m11 = 1F - 2F * nx * nx - 2F * nz * nz
            val m12 = 2F * ny * nz - 2F * nx * nw
            val m20 = 2F * nx * nz - 2F * ny * nw
            val m21 = 2F * ny * nz + 2F * nx * nw
            val m22 = 1F - 2F * nx * nx - 2F * ny * ny
            return Matrix3(m00, m10, m20, m01, m11, m21, m02, m12, m22)
        }
    }
}
