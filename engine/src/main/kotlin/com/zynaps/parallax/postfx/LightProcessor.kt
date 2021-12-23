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
package com.zynaps.parallax.postfx

import com.zynaps.parallax.core.Camera
import com.zynaps.parallax.core.Color
import com.zynaps.parallax.core.Device
import com.zynaps.parallax.core.Light
import com.zynaps.parallax.math.Scalar
import com.zynaps.parallax.system.Parallel.F
import java.util.concurrent.Callable

class LightProcessor(val device: Device) : PostProcessor {

    private val tasks = genTasks()

    var lights = ArrayList<Light>(0)
    var camera: Camera = Camera.Perspective()
    var ambientRGB = 0x000000

    override fun apply() {
        F.invokeAll(tasks)
    }

    private fun genTasks() = (0 until device.height).map {
        Callable {
            val lightsArray = lights.toTypedArray()
            val ambientRed = Color.red(ambientRGB)
            val ambientGrn = Color.grn(ambientRGB)
            val ambientBlu = Color.blu(ambientRGB)
            val screenToWorld = camera.transform.invert
            val twoOverWidth = 2.0F / device.width
            val twoOverHeight = 2.0F / device.height
            val offset = it * device.width
            val vy = -it.toFloat() * twoOverHeight + 1.0F
            var vx = -1.0F
            for (mem in offset until offset + device.width) {
                val z = device.depthBuffer[mem]
                if (z < 1.0F) {
                    val diff = device.colorBuffer[mem]
                    val kdr = Color.red(diff)
                    val kdg = Color.grn(diff)
                    val kdb = Color.blu(diff)

                    val spec = device.specularBuffer[mem]
                    val ksr = Color.red(spec)
                    val ksg = Color.grn(spec)
                    val ksb = Color.blu(spec)
                    val shininess = Color.alpha(spec)

                    val nm = device.normalBuffer[mem]
                    var nx = UNPACK[1023 and nm.shr(20)]
                    var ny = UNPACK[1023 and nm.shr(10)]
                    var nz = UNPACK[1023 and nm]
                    val nl = Scalar.invSqrt(nx * nx + ny * ny + nz * nz)
                    nx *= nl
                    ny *= nl
                    nz *= nl

                    val vz = z * 2.0F - 1.0F
                    val vw = 1.0F / (vx * screenToWorld.m30 + vy * screenToWorld.m31 + vz * screenToWorld.m32 + screenToWorld.m33)
                    val wx = (vx * screenToWorld.m00 + vy * screenToWorld.m01 + vz * screenToWorld.m02 + screenToWorld.m03) * vw
                    val wy = (vx * screenToWorld.m10 + vy * screenToWorld.m11 + vz * screenToWorld.m12 + screenToWorld.m13) * vw
                    val wz = (vx * screenToWorld.m20 + vy * screenToWorld.m21 + vz * screenToWorld.m22 + screenToWorld.m23) * vw

                    var sumRed = 0
                    var sumGrn = 0
                    var sumBlu = 0
                    var specRed = 0
                    var specGrn = 0
                    var specBlu = 0
                    for (l in lightsArray.indices) {
                        val light = lightsArray[l]
                        val result = (light.trace(nx, ny, nz, wx, wy, wz) * 256.0F).toInt()
                        sumRed += light.red * kdr * result shr 16
                        sumGrn += light.grn * kdg * result shr 16
                        sumBlu += light.blu * kdb * result shr 16
                        if (result > 0.0F && (ksr or ksg or ksb) != 0) {
                            val specAngle = specular(camera, light, wx, wy, wz, nx, ny, nz)
                            val specular = (result * pow2(specAngle, shininess)).toInt()
                            specRed += light.red * ksr * specular shr 16
                            specGrn += light.grn * ksg * specular shr 16
                            specBlu += light.blu * ksb * specular shr 16
                        }
                    }

                    val red = (ambientRed * kdr).shr(8) + sumRed + specRed
                    val grn = (ambientGrn * kdg).shr(8) + sumGrn + specGrn
                    val blu = (ambientBlu * kdb).shr(8) + sumBlu + specBlu
                    device.colorBuffer[mem] = Color.pack(red, grn, blu)
                }
                vx += twoOverWidth
            }
        }
    }

    private companion object {
        val UNPACK = (0 until 1024).map { it / 512.0F - 1.0F }.toTypedArray()

        fun pow2(x: Float, y: Int): Float {
            var a = x
            var b = y
            var re = 1.0F
            while (b > 0) {
                if (b and 1 == 1) re *= a
                b = b shr 1
                a *= a
            }
            return re
        }

        fun specular(camera: Camera, light: Light, wx: Float, wy: Float, wz: Float, nx: Float, ny: Float, nz: Float): Float {
            val cx = camera.position.x - wx
            val cy = camera.position.y - wy
            val cz = camera.position.z - wz
            val lx = light.position.x - wx
            val ly = light.position.y - wy
            val lz = light.position.z - wz
            val dot = (nx * lx + ny * ly + nz * lz) * 2F
            val ang = (nx * dot - lx) * cx + (ny * dot - ly) * cy + (nz * dot - lz) * cz
            return if (ang > 0F) ang * Scalar.invSqrt((cx * cx + cy * cy + cz * cz) * (lx * lx + ly * ly + lz * lz)) else 0F
        }
    }
}
