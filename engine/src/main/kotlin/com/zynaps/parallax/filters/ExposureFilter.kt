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
import com.zynaps.parallax.core.Raster
import com.zynaps.parallax.math.Scalar.clamp
import com.zynaps.parallax.system.Parallel
import kotlin.math.exp

class ExposureFilter(src: Raster = Raster.ZERO, dst: Raster = src) : Filter {

    override var source = src
        set(value) {
            field = value
            destination = value
        }

    override var destination = dst

    var exposure = 1.0F
        set(value) {
            field = clamp(value, Float.MIN_VALUE, Float.MAX_VALUE)
        }

    override fun apply() {
        val lut = IntArray(256)
        for (i in 0 until 256) lut[i] = (255.0F * (1.0F - exp(-i / 255.0F * exposure))).toInt()
        Parallel.partition(source.size) { _, from, to ->
            for (x in from until to) {
                val pixel = source[x]
                destination[x] = Color.fastPack(lut[red(pixel)], lut[grn(pixel)], lut[blu(pixel)])
            }
        }
    }
}
