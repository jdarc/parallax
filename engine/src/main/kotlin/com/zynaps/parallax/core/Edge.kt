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

internal class Edge {
    var y1 = 0
    var y2 = 0

    var x = 0F
    var z = 0F
    var _1OverZ = 0F
    var nxOverZ = 0F
    var nyOverZ = 0F
    var nzOverZ = 0F
    var tuOverZ = 0F
    var tvOverZ = 0F

    var xStep = 0F
    var zStep = 0F
    var _1OverZStep = 0F
    var nxOverZStep = 0F
    var nyOverZStep = 0F
    var nzOverZStep = 0F
    var tuOverZStep = 0F
    var tvOverZStep = 0F

    fun configure(g: Gradients, a: Vertex, b: Vertex): Int {
        y1 = max(0, ceil(a.vy))
        val height = ceil(b.vy) - y1

        if (height > 0) {
            y2 = y1 + height

            val yPreStep = y1 - a.vy
            xStep = (b.vx - a.vx) / (b.vy - a.vy)
            x = yPreStep * xStep + a.vx

            val xPreStep = x - a.vx
            z = yPreStep * g._zOverZdY + (xPreStep * g._zOverZdX + a.vz)
            _1OverZ = yPreStep * g._1OverZdY + xPreStep * g._1OverZdX + a.vw
            nxOverZ = yPreStep * g.nxOverZdY + xPreStep * g.nxOverZdX + a.nx
            nyOverZ = yPreStep * g.nyOverZdY + xPreStep * g.nyOverZdX + a.ny
            nzOverZ = yPreStep * g.nzOverZdY + xPreStep * g.nzOverZdX + a.nz
            tuOverZ = yPreStep * g.tuOverZdY + xPreStep * g.tuOverZdX + a.tu
            tvOverZ = yPreStep * g.tvOverZdY + xPreStep * g.tvOverZdX + a.tv

            zStep = xStep * g._zOverZdX + g._zOverZdY
            _1OverZStep = xStep * g._1OverZdX + g._1OverZdY
            nxOverZStep = xStep * g.nxOverZdX + g.nxOverZdY
            nyOverZStep = xStep * g.nyOverZdX + g.nyOverZdY
            nzOverZStep = xStep * g.nzOverZdX + g.nzOverZdY
            tuOverZStep = xStep * g.tuOverZdX + g.tuOverZdY
            tvOverZStep = xStep * g.tvOverZdX + g.tvOverZdY
        }

        return height
    }
}
