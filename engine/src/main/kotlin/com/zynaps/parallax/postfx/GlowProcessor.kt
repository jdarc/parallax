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

import com.zynaps.parallax.core.Raster
import com.zynaps.parallax.filters.BlurFilter
import com.zynaps.parallax.filters.FilterOp
import com.zynaps.parallax.filters.GammaFilter
import com.zynaps.parallax.filters.ResampleFilter
import com.zynaps.parallax.system.Logger

class GlowProcessor : PostProcessor {

    private var tmp = Raster.create(0, 0)
    private val blurFilter = BlurFilter()
    private val gammaFilter = GammaFilter()
    private val resampleFilter = ResampleFilter()

    var source: Raster = Raster.ZERO
    var destination: Raster = Raster.ZERO

    override fun apply() {
        val glowWidth = source.width / 2
        val glowHeight = (glowWidth * source.height) / source.width
        if (tmp.width != glowWidth || tmp.height != glowHeight) {
            Logger.debug("created glow buffer ${glowWidth}x${glowHeight}")
            tmp = Raster.create(glowWidth, glowHeight)
        }

        resampleFilter.source = source
        resampleFilter.destination = tmp
        resampleFilter.op = FilterOp.SOURCE
        resampleFilter.apply()

        gammaFilter.source = tmp
        gammaFilter.destination = tmp
        gammaFilter.gamma = 2.2F
        gammaFilter.apply()

        blurFilter.source = tmp
        blurFilter.destination = tmp
        blurFilter.radius = 2
        blurFilter.steps = 2
        blurFilter.apply()

        gammaFilter.gamma = 1.2F
        gammaFilter.apply()

        resampleFilter.source = tmp
        resampleFilter.destination = destination
        resampleFilter.op = FilterOp.ADD
        resampleFilter.apply()
    }
}
