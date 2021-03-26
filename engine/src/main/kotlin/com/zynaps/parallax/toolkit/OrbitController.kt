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
package com.zynaps.parallax.toolkit

import com.zynaps.parallax.core.Camera
import com.zynaps.parallax.math.Matrix4
import com.zynaps.parallax.math.Scalar
import com.zynaps.parallax.math.Scalar.max
import com.zynaps.parallax.math.Vector3
import java.awt.AWTEvent
import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent

class OrbitController(private val camera: Camera) : Controller {
    private val last = Point(0, 0)
    private var yaw = camera.yaw
    private var pitch = camera.pitch
    private var distance = camera.position.length

    override fun handleEvent(event: AWTEvent) {
        when (event.id) {
            MouseEvent.MOUSE_MOVED -> {
                val mouseEvent = event as MouseEvent
                last.x = mouseEvent.x
                last.y = mouseEvent.y
            }
            MouseEvent.MOUSE_DRAGGED -> {
                val mouseEvent = event as MouseEvent
                yaw -= (mouseEvent.x - last.x) * 0.01F
                pitch = Scalar.clamp(pitch - (mouseEvent.y - last.y) * 0.01F, -1.57F, 1.57F)
                last.x = mouseEvent.x
                last.y = mouseEvent.y
            }
            MouseWheelEvent.MOUSE_WHEEL -> {
                val mouseWheelEvent = event as MouseWheelEvent
                distance = max(1.0F, distance + mouseWheelEvent.unitsToScroll)
            }
        }
    }

    override fun update(seconds: Double, speed: Float) {
        camera.moveTo(Matrix4.createRotationY(yaw) * Matrix4.createRotationX(pitch) * Vector3(0F, 0F, distance))
        camera.lookAt(Vector3.ZERO)
    }
}
