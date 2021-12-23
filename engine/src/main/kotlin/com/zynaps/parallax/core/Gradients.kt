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

internal class Gradients {

    var _1OverZdX = 0.0F
    var _1OverZdY = 0.0F
    var _zOverZdX = 0.0F
    var _zOverZdY = 0.0F
    var nxOverZdX = 0.0F
    var nxOverZdY = 0.0F
    var nyOverZdX = 0.0F
    var nyOverZdY = 0.0F
    var nzOverZdX = 0.0F
    var nzOverZdY = 0.0F
    var tuOverZdX = 0.0F
    var tuOverZdY = 0.0F
    var tvOverZdX = 0.0F
    var tvOverZdY = 0.0F

    fun configure(a: Vertex, c: Vertex, b: Vertex) {
        val acx = a.vx - c.vx
        val bcx = b.vx - c.vx
        val acy = a.vy - c.vy
        val bcy = b.vy - c.vy
        val oneOverDX = 1.0F / (bcx * acy - acx * bcy)

        val w0 = a.vw - c.vw
        val w1 = b.vw - c.vw
        _1OverZdX = oneOverDX * (w1 * acy - w0 * bcy)
        _1OverZdY = oneOverDX * (w0 * bcx - w1 * acx)

        val z0 = a.vz - c.vz
        val z1 = b.vz - c.vz
        _zOverZdX = oneOverDX * (z1 * acy - z0 * bcy)
        _zOverZdY = oneOverDX * (z0 * bcx - z1 * acx)

        val nx0 = a.nx - c.nx
        val nx1 = b.nx - c.nx
        nxOverZdX = oneOverDX * (nx1 * acy - nx0 * bcy)
        nxOverZdY = oneOverDX * (nx0 * bcx - nx1 * acx)

        val ny0 = a.ny - c.ny
        val ny1 = b.ny - c.ny
        nyOverZdX = oneOverDX * (ny1 * acy - ny0 * bcy)
        nyOverZdY = oneOverDX * (ny0 * bcx - ny1 * acx)

        val nz0 = a.nz - c.nz
        val nz1 = b.nz - c.nz
        nzOverZdX = oneOverDX * (nz1 * acy - nz0 * bcy)
        nzOverZdY = oneOverDX * (nz0 * bcx - nz1 * acx)

        val tu0 = a.tu - c.tu
        val tu1 = b.tu - c.tu
        tuOverZdX = oneOverDX * (tu1 * acy - tu0 * bcy)
        tuOverZdY = oneOverDX * (tu0 * bcx - tu1 * acx)

        val tv0 = a.tv - c.tv
        val tv1 = b.tv - c.tv
        tvOverZdX = oneOverDX * (tv1 * acy - tv0 * bcy)
        tvOverZdY = oneOverDX * (tv0 * bcx - tv1 * acx)
    }
}
