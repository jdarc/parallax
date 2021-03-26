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
package com.zynaps.parallax.filters

import com.zynaps.parallax.core.Color
import com.zynaps.parallax.core.RenderBuffer
import com.zynaps.parallax.core.RenderBuffer.Companion.ZERO
import com.zynaps.parallax.math.Scalar.ceil
import com.zynaps.parallax.math.Scalar.clamp
import com.zynaps.parallax.math.Scalar.min
import com.zynaps.parallax.system.Parallel

class ResampleFilter(src: RenderBuffer = ZERO, dst: RenderBuffer = src, var op: FilterOp = FilterOp.SOURCE) : Filter {

    override var source = src
        set(value) {
            field = value
            destination = value
        }

    override var destination = dst

    override fun apply() {
        val cb = mutableListOf<() -> Unit>()
        val blockSize = 64
        val dx = ceil(destination.width / blockSize.toFloat())
        val dy = ceil(destination.height / blockSize.toFloat())
        for (y in 0 until dy) {
            val sy1 = y * blockSize
            val sy2 = min(destination.height, sy1 + blockSize)
            for (x in 0 until dx) {
                val sx1 = x * blockSize
                val sx2 = min(destination.width, sx1 + blockSize)
                cb.add { block(sx1, sy1, sx2, sy2, source, destination, op) }
            }
        }
        Parallel.invoke(*cb.toTypedArray())
    }

    private fun block(sx1: Int, sy1: Int, sx2: Int, sy2: Int, src: RenderBuffer, dst: RenderBuffer, op: FilterOp) {
        val xRatio = (src.width.shl(12) + 2048) / dst.width
        val yRatio = (src.height.shl(12) + 2048) / dst.height
        for (i in sy1 until sy2) {
            val y = yRatio * i
            val oy = y ushr 12
            val by = y and 0xFFF ushr 4
            val y1 = src.width * clamp(oy + 0, 0, src.height - 1)
            val y2 = src.width * clamp(oy + 1, 0, src.height - 1)
            val mem = i * dst.width
            for (j in sx1 until sx2) {
                val x = xRatio * j
                val ox = x ushr 12
                val x1 = clamp(ox + 0, 0, src.width - 1)
                val x2 = clamp(ox + 1, 0, src.width - 1)
                val a = src[y1 + x1]
                val c = src[y2 + x1]
                val b = src[y1 + x2]
                val d = src[y2 + x2]
                val srcRgb = if (a == c && b == d && a == d) a else {
                    val bx = x and 0xFFF ushr 4
                    val weight1 = (256 - bx) * (256 - by) ushr 8
                    val weight4 = bx * by ushr 8
                    val weight2 = bx - weight4
                    val weight3 = by - weight4
                    val n1 = weight1 * Color.explode(a)
                    val n2 = weight2 * Color.explode(b)
                    val n3 = weight3 * Color.explode(c)
                    val n4 = weight4 * Color.explode(d)
                    val sum = (n1 + n2 ushr 8) + (n3 + n4 ushr 8)
                    (sum.ushr(24) and 0xFF00FF00 or (sum and 0xFF00FF)).toInt()
                }
                dst[mem + j] = op.apply(srcRgb, dst[mem + j])
            }
        }
    }
}
