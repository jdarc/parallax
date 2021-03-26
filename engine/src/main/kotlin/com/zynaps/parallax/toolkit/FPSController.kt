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
import com.zynaps.parallax.math.Scalar.clamp
import com.zynaps.parallax.math.Scalar.cos
import com.zynaps.parallax.math.Scalar.sin
import com.zynaps.parallax.math.Vector3
import java.awt.AWTEvent
import java.awt.Point
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent

class FPSController(private val camera: Camera) : Controller {
    private val last = Point(0, 0)
    private var yaw = camera.yaw
    private var pitch = camera.pitch
    private var movement = 0

    override fun handleEvent(event: AWTEvent) {
        when (event.id) {
            KeyEvent.KEY_PRESSED -> {
                when ((event as KeyEvent).keyCode) {
                    KeyEvent.VK_W -> movement = movement or MOVEMENT_FORWARD
                    KeyEvent.VK_S -> movement = movement or MOVEMENT_BACK
                    KeyEvent.VK_A -> movement = movement or MOVEMENT_LEFT
                    KeyEvent.VK_D -> movement = movement or MOVEMENT_RIGHT
                    KeyEvent.VK_UP -> movement = movement or MOVEMENT_UP
                    KeyEvent.VK_DOWN -> movement = movement or MOVEMENT_DOWN
                }
            }
            KeyEvent.KEY_RELEASED -> {
                when ((event as KeyEvent).keyCode) {
                    KeyEvent.VK_W -> movement = movement xor MOVEMENT_FORWARD
                    KeyEvent.VK_S -> movement = movement xor MOVEMENT_BACK
                    KeyEvent.VK_A -> movement = movement xor MOVEMENT_LEFT
                    KeyEvent.VK_D -> movement = movement xor MOVEMENT_RIGHT
                    KeyEvent.VK_UP -> movement = movement xor MOVEMENT_UP
                    KeyEvent.VK_DOWN -> movement = movement xor MOVEMENT_DOWN
                }
            }
            MouseEvent.MOUSE_MOVED -> {
                val mouseEvent = event as MouseEvent
                last.x = mouseEvent.x
                last.y = mouseEvent.y
            }
            MouseEvent.MOUSE_DRAGGED -> {
                val mouseEvent = event as MouseEvent
                yaw -= (mouseEvent.x - last.x) * 0.01F
                pitch = clamp(pitch - (mouseEvent.y - last.y) * 0.01F, -1.57F, 1.57F)
                last.x = mouseEvent.x
                last.y = mouseEvent.y
            }
        }
    }

    override fun update(seconds: Double, speed: Float) {
        val rate = (seconds * speed).toFloat()

        if (movement and MOVEMENT_FORWARD == MOVEMENT_FORWARD) {
            camera.moveForward(rate)
        } else if (movement and MOVEMENT_BACK == MOVEMENT_BACK) {
            camera.moveBackward(rate)
        }

        if (movement and MOVEMENT_LEFT == MOVEMENT_LEFT) {
            camera.moveLeft(rate)
        } else if (movement and MOVEMENT_RIGHT == MOVEMENT_RIGHT) {
            camera.moveRight(rate)
        }

        if (movement and MOVEMENT_UP == MOVEMENT_UP) {
            camera.moveUp(rate)
        } else if (movement and MOVEMENT_DOWN == MOVEMENT_DOWN) {
            camera.moveDown(rate)
        }

        val lookAt = Vector3(-sin(yaw) * cos(pitch), sin(pitch), -cos(yaw) * cos(pitch))
        camera.orient(lookAt)
    }

    companion object {
        private const val MOVEMENT_FORWARD = 1
        private const val MOVEMENT_BACK = 2
        private const val MOVEMENT_LEFT = 4
        private const val MOVEMENT_RIGHT = 8
        private const val MOVEMENT_UP = 16
        private const val MOVEMENT_DOWN = 32
    }
}
