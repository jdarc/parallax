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

import com.zynaps.parallax.math.Scalar.ONE
import com.zynaps.parallax.math.Scalar.ceil
import com.zynaps.parallax.math.Scalar.max
import com.zynaps.parallax.math.Scalar.min
import com.zynaps.parallax.system.Parallel.F
import java.util.concurrent.Callable

internal class ColorRasterizer : Rasterizer {

    private var tasks = listOf<Callable<Unit>>()

    override fun clear(device: Device, color: Int, depth: Float) {
        device.colorBuffer.fill(color)
        device.glowBuffer.fill(0x000000)
    }

    override fun render(device: Device) {
        if (tasks.size != device.height) tasks = genTasks(device)
        F.invokeAll(tasks)
    }

    private fun genTasks(device: Device) = (0 until device.height).map { y ->
        val yy = y.toFloat()
        val colrBuffer = device.colorBuffer
        val specBuffer = device.specularBuffer
        val emitBuffer = device.glowBuffer
        val normBuffer = device.normalBuffer
        val offset = y * device.width
        val buffers = device.spanBuffers.map { it[y] }
        Callable {
            buffers.forEach { spanBuffer ->
                for (i in 0 until spanBuffer.size) {
                    val fragment = spanBuffer[i]
                    val diff = fragment.material.diffuse
                    val spec = fragment.material.specular
                    val emit = fragment.material.emissive
                    val glos = fragment.material.glossiness shl 24
                    val delta = yy - fragment.leftY
                    val lx = fragment.getLeftX(delta)
                    val x1 = max(0, ceil(lx))
                    val x2 = min(device.width, ceil(fragment.getRightX(yy - fragment.rightY)))
                    val preStep = x1 - lx
                    var z1 = fragment._zOverZdX * preStep + fragment.getZ(delta)
                    var _1OverZ = fragment._1OverZdX * preStep + fragment.get1OverZ(delta)
                    var nxOverZ = fragment.nxOverZdX * preStep + fragment.getNxOverZ(delta)
                    var nyOverZ = fragment.nyOverZdX * preStep + fragment.getNyOverZ(delta)
                    var nzOverZ = fragment.nzOverZdX * preStep + fragment.getNzOverZ(delta)
                    var tuOverZ = fragment.tuOverZdX * preStep + fragment.getTuOverZ(delta)
                    var tvOverZ = fragment.tvOverZdX * preStep + fragment.getTvOverZ(delta)
                    for (x in offset + x1 until offset + x2) {
                        if (z1 <= device.depthBuffer[x]) {
                            val oz = ONE / _1OverZ
                            val nz = 511.0f * oz
                            val tu = tuOverZ * oz
                            val tv = tvOverZ * oz
                            colrBuffer[x] = diff.sample(tu, tv)
                            specBuffer[x] = spec.sample(tu, tv) or glos
                            emitBuffer[x] = emit.sample(tu, tv)
                            normBuffer[x] = packNormals(nxOverZ * nz, nyOverZ * nz, nzOverZ * nz)
                        }
                        z1 += fragment._zOverZdX
                        _1OverZ += fragment._1OverZdX
                        nxOverZ += fragment.nxOverZdX
                        nyOverZ += fragment.nyOverZdX
                        nzOverZ += fragment.nzOverZdX
                        tuOverZ += fragment.tuOverZdX
                        tvOverZ += fragment.tvOverZdX
                    }
                }
            }
        }
    }

    private companion object {
        private fun packNormals(x: Float, y: Float, z: Float): Int {
            val r = (x + 512.0f).toInt()
            val g = (y + 512.0f).toInt()
            val b = (z + 512.0f).toInt()
            return r.shl(20) or g.shl(10) or b
        }
    }
}
