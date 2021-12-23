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

import com.zynaps.parallax.core.RenderBuffer
import com.zynaps.parallax.math.Scalar.ceil
import com.zynaps.parallax.math.Scalar.clamp
import com.zynaps.parallax.math.Scalar.min
import com.zynaps.parallax.math.Scalar.round
import com.zynaps.parallax.system.Parallel.CPUS
import com.zynaps.parallax.system.Parallel.F
import java.util.concurrent.Callable
import kotlin.math.pow

class GammaFilter(src: RenderBuffer = RenderBuffer.ZERO, dst: RenderBuffer = src) : Filter {

    override var source = src
        set(value) {
            field = value
            destination = value
        }

    override var destination = dst

    var gamma = 1.2F
        set(value) {
            field = clamp(value, Float.MIN_VALUE, Float.MAX_VALUE)
        }

    override fun apply() {
        F.invokeAll(genTasks(ceil(source.size.toFloat() / CPUS)))
    }

    private fun genTasks(size: Int) = (0 until source.size step size).map {
        Callable {
            val lut = IntArray(256) { round((it / 255.0F).pow(1.0F / gamma) * 255.0F) }
            for (x in it until it + min(size, source.size - it)) {
                val pixel = source[x]
                val red = lut[0xFF and pixel.shr(16)].shl(16)
                val grn = lut[0xFF and pixel.shr(8)].shl(8)
                val blu = lut[0xFF and pixel]
                destination[x] = red or grn or blu
            }
        }
    }
}
