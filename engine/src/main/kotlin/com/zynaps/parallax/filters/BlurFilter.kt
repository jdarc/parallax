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
import com.zynaps.parallax.core.Color.blu
import com.zynaps.parallax.core.Color.grn
import com.zynaps.parallax.core.Color.red
import com.zynaps.parallax.core.RenderBuffer
import com.zynaps.parallax.math.Scalar.clamp
import com.zynaps.parallax.math.Scalar.max
import com.zynaps.parallax.math.Scalar.min
import com.zynaps.parallax.system.Parallel
import java.util.stream.IntStream

class BlurFilter(src: RenderBuffer = RenderBuffer.ZERO, dst: RenderBuffer = src) : Filter {

    private var tmp = IntArray(0)

    override var source = src
        set(value) {
            field = value
            destination = value
        }

    override var destination = dst

    var radius = 8
        set(value) {
            field = clamp(value, 0, Int.MAX_VALUE)
        }

    var steps = 3
        set(value) {
            field = clamp(value, 0, Int.MAX_VALUE)
        }

    override fun apply() {
        if (radius <= 0) return
        val radius2 = radius + radius + 1
        val dv = IntStream.range(0, 256 * radius2).map { it / radius2 }.toArray()
        if (tmp.size != source.size) tmp = IntArray(source.size)
        for (times in 0 until steps) {
            Parallel.execute(source.height) { horiz(dv, radius, it * source.width, source.width - 1) }
            Parallel.execute(source.width) { vert(dv, radius, it, source.height - 1) }
        }
    }

    private fun horiz(dv: IntArray, radius: Int, offset: Int, width: Int) {
        var rsum = 0
        var gsum = 0
        var bsum = 0
        var rgb = source[offset] and 0xFFFFFF
        if (rgb != 0) {
            rsum = radius * red(rgb)
            gsum = radius * grn(rgb)
            bsum = radius * blu(rgb)
        }

        for (i in 0..radius) {
            rgb = source[offset + i] and 0xFFFFFF
            if (rgb == 0) continue
            rsum += red(rgb)
            gsum += grn(rgb)
            bsum += blu(rgb)
        }

        var last = Color.fastPack(dv[rsum], dv[gsum], dv[bsum])
        for (x in 0..width) {
            tmp[offset + x] = last
            val p1 = source[offset + min(width, x + radius + 1)] and 0xFFFFFF
            val p2 = source[offset + max(0, x - radius)] and 0xFFFFFF
            if (p1 == 0 && p2 == 0) continue
            rsum += red(p1) - red(p2)
            gsum += grn(p1) - grn(p2)
            bsum += blu(p1) - blu(p2)
            last = Color.fastPack(dv[rsum], dv[gsum], dv[bsum])
        }
    }

    private fun vert(dv: IntArray, radius: Int, offset: Int, height: Int) {
        val srcWidth = source.width
        val baseRgb = tmp[offset]
        val baseRed = red(baseRgb)
        val baseGrn = grn(baseRgb)
        val baseBlu = blu(baseRgb)
        var rsum = 0
        var gsum = 0
        var bsum = 0
        for (i in 0 until radius) {
            rsum += baseRed
            gsum += baseGrn
            bsum += baseBlu
            val sumRgb = tmp[offset + i * srcWidth] and 0xFFFFFF
            if (sumRgb == 0) continue
            rsum += red(sumRgb)
            gsum += grn(sumRgb)
            bsum += blu(sumRgb)
        }
        val sumRgb = tmp[offset + radius * srcWidth]
        rsum += red(sumRgb)
        gsum += grn(sumRgb)
        bsum += blu(sumRgb)
        var last = Color.fastPack(dv[rsum], dv[gsum], dv[bsum])
        for (y in 0..height) {
            destination[offset + y * srcWidth] = last
            val dst1 = tmp[offset + srcWidth * min(height, y + radius + 1)] and 0xFFFFFF
            val dst2 = tmp[offset + srcWidth * max(0, y - radius)] and 0xFFFFFF
            if (dst1 == 0 && dst2 == 0) continue
            if (dst1 != 0) {
                rsum += red(dst1)
                gsum += grn(dst1)
                bsum += blu(dst1)
            }
            if (dst2 != 0) {
                rsum -= red(dst2)
                gsum -= grn(dst2)
                bsum -= blu(dst2)
            }
            last = Color.fastPack(dv[rsum], dv[gsum], dv[bsum])
        }
    }
}
