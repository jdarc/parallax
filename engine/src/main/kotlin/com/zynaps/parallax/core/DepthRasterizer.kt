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

import com.zynaps.parallax.math.Scalar.ceil
import com.zynaps.parallax.math.Scalar.max
import com.zynaps.parallax.math.Scalar.min
import com.zynaps.parallax.system.Parallel.F
import java.util.concurrent.Callable

internal class DepthRasterizer : Rasterizer {

    private var tasks = listOf<Callable<Unit>>()

    override fun clear(device: Device, color: Int, depth: Float) = device.depthBuffer.fill(depth)

    override fun render(device: Device) {
        if (tasks.size != device.height) tasks = genTasks(device)
        F.invokeAll(tasks)
    }

    private fun genTasks(device: Device) = (0 until device.height).map { y ->
        val yy = y.toFloat()
        val offset = y * device.width
        val buffers = device.spanBuffers.map { it[y] }
        Callable {
            buffers.forEach { spanBuffer ->
                for (i in 0 until spanBuffer.size) {
                    val fragment = spanBuffer[i]
                    val lx = fragment.getLeftX(yy - fragment.leftY)
                    val x1 = max(0, ceil(lx))
                    val x2 = min(device.width, ceil(fragment.getRightX(yy - fragment.rightY)))
                    var sz = fragment._zOverZdX * (x1 - lx) + fragment.getZ(yy - fragment.leftY)
                    for (x in offset + x1 until offset + x2) {
                        device.depthBuffer[x] = min(sz, device.depthBuffer[x])
                        sz += fragment._zOverZdX
                    }
                }
            }
        }
    }
}
