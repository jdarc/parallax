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
import com.zynaps.parallax.math.Scalar
import com.zynaps.parallax.math.Scalar.asin
import com.zynaps.parallax.math.Scalar.atan2
import com.zynaps.parallax.math.Scalar.clamp
import com.zynaps.parallax.math.Vector3

@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class Camera(near: Float = 1.0F, far: Float = 1000.0F) {

    private var eye = Vector3.UNIT_Z
    private var at = Vector3.ZERO
    private var up = Vector3.UNIT_Y

    var nearDistance = near
        set(value) {
            field = clamp(value, Float.MIN_VALUE, Float.MAX_VALUE)
            if (nearDistance >= farDistance) throw RuntimeException("near distance must be less than far distance")
        }

    var farDistance = far
        set(value) {
            field = clamp(value, 1.0F, Float.MAX_VALUE)
            if (farDistance <= nearDistance) throw RuntimeException("far distance must be greater than near distance")
        }

    val position get() = eye

    val center get() = at

    val direction get() = (at - eye).normalize()

    val transform get() = projection * view

    val yaw: Float
        inline get() {
            view.apply { return -atan2(-this.m20, this.m22) }
        }

    val pitch inline get() = -asin(view.m21)

    val view get() = Matrix4.createLookAt(eye, at, up)

    abstract val projection: Matrix4

    fun moveTo(v: Vector3) = moveTo(v.x, v.y, v.z)

    fun lookAt(v: Vector3) = lookAt(v.x, v.y, v.z)

    fun worldUp(v: Vector3) = worldUp(v.x, v.y, v.z)

    fun moveTo(x: Float, y: Float, z: Float) {
        eye = Vector3(x, y, z)
    }

    fun lookAt(x: Float, y: Float, z: Float) {
        at = Vector3(x, y, z)
    }

    fun worldUp(x: Float, y: Float, z: Float) {
        up = Vector3(x, y, z).normalize()
    }

    private fun strafe(dx: Float, dy: Float, dz: Float) {
        moveTo(eye.x - dx, eye.y - dy, eye.z - dz)
        lookAt(at.x - dx, at.y - dy, at.z - dz)
    }

    fun moveDown(amount: Float) {
        view.apply { strafe(m10 * amount, m11 * amount, m12 * amount) }
    }

    fun moveForward(amount: Float) {
        view.apply { strafe(m20 * amount, m21 * amount, m22 * amount) }
    }

    fun moveLeft(amount: Float) {
        view.apply { strafe(m00 * amount, m01 * amount, m02 * amount) }
    }

    fun moveUp(amount: Float) = moveDown(-amount)

    fun moveBackward(amount: Float) = moveForward(-amount)

    fun moveRight(amount: Float) = moveLeft(-amount)

    fun orient(dir: Vector3) = lookAt(eye.x + dir.x, eye.y + dir.y, eye.z + dir.z)

    class Perspective(fov: Float = Scalar.PI / 4.0F, aspect: Float = 1.0F, near: Float = 1.0F, far: Float = 1000.0F) : Camera(near, far) {

        var fieldOfView = fov
            set(value) {
                field = clamp(value, 0.0001F, 3.1415F)
            }

        var aspectRatio = aspect
            set(value) {
                field = clamp(value, Float.MIN_VALUE, Float.MAX_VALUE)
            }

        override val projection get() = Matrix4.createPerspectiveFov(fieldOfView, aspectRatio, nearDistance, farDistance)
    }

    class Orthographic(width: Float = 100.0F, height: Float = 100.0F, near: Float = 1.0F, far: Float = 1000.0F) : Camera(near, far) {
        var top = height * 0.5F
        var right = width * 0.5F
        var bottom = -height * 0.5F
        var left = -width * 0.5F

        val width get() = right - left
        val height get() = top - bottom

        override val projection get() = Matrix4.createOrthographic(left, right, bottom, top, nearDistance, farDistance)
    }
}
