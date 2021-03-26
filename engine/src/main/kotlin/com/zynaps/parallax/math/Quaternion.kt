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
import com.zynaps.parallax.math.Scalar.cos
import com.zynaps.parallax.math.Scalar.sin
import com.zynaps.parallax.math.Scalar.sqrt

@Suppress("unused", "MemberVisibilityCanBePrivate", "DuplicatedCode", "SpellCheckingInspection", "NOTHING_TO_INLINE")
data class Quaternion(val x: Float, val y: Float, val z: Float, val w: Float) {

    val isIdentity get() = x == 0.0F && y == 0.0F && z == 0.0F && w == 1.0F

    val length get() = sqrt(lengthSquared)

    val lengthSquared get() = (x * x) + (y * y) + (z * z) + (w * w)

    constructor(vector: Vector3, scalar: Float) : this(vector.x, vector.y, vector.z, scalar)

    constructor(src: FloatArray, offset: Int = 0) : this(src[offset + 0], src[offset + 1], src[offset + 2], src[offset + 3])

    inline operator fun unaryMinus() = Quaternion(-x, -y, -z, -w)

    inline operator fun plus(rhs: Quaternion) = Quaternion(x + rhs.x, y + rhs.y, z + rhs.z, w + rhs.w)

    inline operator fun minus(rhs: Quaternion) = Quaternion(x - rhs.x, y - rhs.y, z - rhs.z, w - rhs.w)

    inline operator fun times(rhs: Float) = Quaternion(x * rhs, y * rhs, z * rhs, w * rhs)

    inline operator fun times(rhs: Quaternion): Quaternion {
        val cx = (x * rhs.w) + (w * rhs.x) + (y * rhs.z) - (z * rhs.y)
        val cy = (y * rhs.w) + (w * rhs.y) + (z * rhs.x) - (x * rhs.z)
        val cz = (z * rhs.w) + (w * rhs.z) + (x * rhs.y) - (y * rhs.x)
        val cw = (w * rhs.w) - (x * rhs.x) - (y * rhs.y) - (z * rhs.z)
        return Quaternion(cx, cy, cz, cw)
    }

    inline operator fun div(rhs: Float) = Quaternion(x / rhs, y / rhs, z / rhs, w / rhs)

    inline operator fun div(rhs: Quaternion): Quaternion {
        val ls = rhs.lengthSquared
        val qx = -rhs.x / ls
        val qy = -rhs.y / ls
        val qz = -rhs.z / ls
        val qw = rhs.w / ls
        val dx = (x * qw) + (w * qx) + (y * qz) - (z * qy)
        val dy = (y * qw) + (w * qy) + (z * qx) - (x * qz)
        val dz = (z * qw) + (w * qz) + (x * qy) - (y * qx)
        val dw = (w * qw) - (x * qx) - (y * qy) - (z * qz)
        return Quaternion(dx, dy, dz, dw)
    }

    inline operator fun get(index: Int) = when (index) {
        0 -> x; 1 -> y; 2 -> z; 3 -> w; else -> throw IndexOutOfBoundsException()
    }

    fun angleTo(rhs: Quaternion) = 2.0F * acos(abs(dot(rhs).coerceIn(-1.0F, 1.0F)))

    fun conjugate() = Quaternion(-x, -y, -z, w)

    fun dot(rhs: Quaternion) = (x * rhs.x) + (y * rhs.y) + (z * rhs.z) + (w * rhs.w)

    fun equals(rhs: Quaternion, epsilon: Float = EPSILON): Boolean {
        return Scalar.equals(x, rhs.x, epsilon) && Scalar.equals(y, rhs.y, epsilon) &&
                Scalar.equals(z, rhs.z, epsilon) && Scalar.equals(w, rhs.w, epsilon)
    }

    fun inverse(): Quaternion {
        val lenSqr = lengthSquared
        if (lenSqr <= 0.0F) return ZERO
        val invLenSq = 1.0F / lenSqr
        return Quaternion(-x * invLenSq, -y * invLenSq, -z * invLenSq, w * invLenSq)
    }

    fun lerp(rhs: Quaternion, alpha: Float): Quaternion {
        val a = if (dot(rhs) < 0.0F) -alpha else alpha
        val t = 1.0F - alpha
        val x = (t * x) + (a * rhs.x)
        val y = (t * y) + (a * rhs.y)
        val z = (t * z) + (a * rhs.z)
        val w = (t * w) + (a * rhs.w)
        val invLen = 1.0F / sqrt(x * x + y * y + z * z + w * w)
        return Quaternion(x * invLen, y * invLen, z * invLen, w * invLen)
    }

    fun normalize(): Quaternion {
        val lenSqr = lengthSquared
        return if (lenSqr > 0.0F) this * (1.0F / sqrt(lenSqr)) else ZERO
    }

    fun slerp(rhs: Quaternion, alpha: Float): Quaternion {
        var dot = dot(rhs)
        var flip = 1.0F
        if (dot < 0.0F) {
            flip = -1.0F
            dot = -dot
        }
        val s1: Float
        val s2: Float
        if ((1.0F - dot) > EPSILON) {
            val omega = acos(dot)
            val sinOmega = sin(omega)
            s1 = sin((1.0F - alpha) * omega) / sinOmega
            s2 = flip * sin(alpha * omega) / sinOmega
        } else {
            s1 = 1.0F - alpha
            s2 = flip * alpha
        }
        val w = (s1 * w) + (s2 * rhs.w)
        val x = (s1 * x) + (s2 * rhs.x)
        val y = (s1 * y) + (s2 * rhs.y)
        val z = (s1 * z) + (s2 * rhs.z)
        return Quaternion(x, y, z, w)
    }

    fun toArray(dst: FloatArray, offset: Int = 0) {
        dst[offset + 0] = x
        dst[offset + 1] = y
        dst[offset + 2] = z
        dst[offset + 3] = w
    }

    companion object {

        val ZERO = Quaternion(0.0F, 0.0F, 0.0F, 0.0F)

        val IDENTITY = Quaternion(0.0F, 0.0F, 0.0F, 1.0F)

        fun create(x: Float, y: Float, z: Float, w: Float): Quaternion {
            val invLen = 1.0F / sqrt(x * x + y * y + z * z + w * w)
            return Quaternion(x * invLen, y * invLen, z * invLen, w * invLen)
        }

        fun create(v: Vector4) = create(v.x, v.y, v.z, v.w)

        fun createFromAxisAngle(x: Float, y: Float, z: Float, angle: Float): Quaternion {
            val len = sqrt(x * x + y * y + z * z)
            if (len < EPSILON) return ZERO
            val mag = sin(angle * 0.5F) / len
            val qx = x * mag
            val qy = y * mag
            val qz = z * mag
            val qw = cos(angle * 0.5F)
            return Quaternion(qx, qy, qz, qw)
        }

        fun createFromAxisAngle(a: Vector3, angle: Float) = createFromAxisAngle(a.x, a.y, a.z, angle)

        fun createFromYawPitchRoll(yaw: Float, pitch: Float, roll: Float): Quaternion {
            val sr = sin(roll * 0.5F)
            val cr = cos(roll * 0.5F)
            val sp = sin(pitch * 0.5F)
            val cp = cos(pitch * 0.5F)
            val sy = sin(yaw * 0.5F)
            val cy = cos(yaw * 0.5F)
            val x = (cy * sp * cr) + (sy * cp * sr)
            val y = (sy * cp * cr) - (cy * sp * sr)
            val z = (cy * cp * sr) - (sy * sp * cr)
            val w = (cy * cp * cr) + (sy * sp * sr)
            return Quaternion(x, y, z, w)
        }

        fun createFromRotationMatrix(matrix: Matrix4): Quaternion {
            val tr = matrix.m00 + matrix.m11 + matrix.m22
            if (tr > 0.0F) {
                val s = sqrt(tr + 1.0F) * 2.0F
                val qw = 0.25F * s
                val qx = (matrix.m21 - matrix.m12) / s
                val qy = (matrix.m02 - matrix.m20) / s
                val qz = (matrix.m10 - matrix.m01) / s
                return Quaternion(qx, qy, qz, qw)
            } else if ((matrix.m00 > matrix.m11) && (matrix.m00 > matrix.m22)) {
                val s = sqrt(1.0F + matrix.m00 - matrix.m11 - matrix.m22) * 2.0F
                val qw = (matrix.m21 - matrix.m12) / s
                val qx = 0.25F * s
                val qy = (matrix.m01 + matrix.m10) / s
                val qz = (matrix.m02 + matrix.m20) / s
                return Quaternion(qx, qy, qz, qw)
            } else if (matrix.m11 > matrix.m22) {
                val s = sqrt(1.0F + matrix.m11 - matrix.m00 - matrix.m22) * 2.0F
                val qw = (matrix.m02 - matrix.m20) / s
                val qx = (matrix.m01 + matrix.m10) / s
                val qy = 0.25F * s
                val qz = (matrix.m12 + matrix.m21) / s
                return Quaternion(qx, qy, qz, qw)
            } else {
                val s = sqrt(1.0F + matrix.m22 - matrix.m00 - matrix.m11) * 2.0F
                val qw = (matrix.m10 - matrix.m01) / s
                val qx = (matrix.m02 + matrix.m20) / s
                val qy = (matrix.m12 + matrix.m21) / s
                val qz = 0.25F * s
                return Quaternion(qx, qy, qz, qw)
            }
        }
    }
}
