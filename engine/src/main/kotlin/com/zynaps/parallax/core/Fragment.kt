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

internal class Fragment {
    var y = 0

    private var leftX = 0F
    private var rightX = 0F

    var leftXStep = 0F
    var rightXStep = 0F

    private var leftZ = 0F
    private var leftZStep = 0F

    var _1OverZdX = 0F
    var _zOverZdX = 0F
    var nxOverZdX = 0F
    var nyOverZdX = 0F
    var nzOverZdX = 0F
    var tuOverZdX = 0F
    var tvOverZdX = 0F

    private var _1OverZ = 0F
    private var nxOverZ = 0F
    private var nyOverZ = 0F
    private var nzOverZ = 0F
    private var tuOverZ = 0F
    private var tvOverZ = 0F

    private var _1OverZStep = 0F
    private var nxOverZStep = 0F
    private var nyOverZStep = 0F
    private var nzOverZStep = 0F
    private var tuOverZStep = 0F
    private var tvOverZStep = 0F

    var material = Material.DEFAULT

    fun configure(m: Material, g: Gradients, l: Edge, r: Edge): Fragment {
        material = m

        _1OverZdX = g._1OverZdX
        _zOverZdX = g._zOverZdX
        nxOverZdX = g.nxOverZdX
        nyOverZdX = g.nyOverZdX
        nzOverZdX = g.nzOverZdX
        tuOverZdX = g.tuOverZdX
        tvOverZdX = g.tvOverZdX

        y = r.y1

        rightX = r.x
        rightXStep = r.xStep

        val diff = (r.y1 - l.y1).toFloat()

        leftX = l.x + l.xStep * diff
        leftXStep = l.xStep

        leftZ = l.z + l.zStep * diff
        leftZStep = l.zStep

        _1OverZ = l._1OverZ + l._1OverZStep * diff
        nxOverZ = l.nxOverZ + l.nxOverZStep * diff
        nyOverZ = l.nyOverZ + l.nyOverZStep * diff
        nzOverZ = l.nzOverZ + l.nzOverZStep * diff
        tuOverZ = l.tuOverZ + l.tuOverZStep * diff
        tvOverZ = l.tvOverZ + l.tvOverZStep * diff

        _1OverZStep = l._1OverZStep
        nxOverZStep = l.nxOverZStep
        nyOverZStep = l.nyOverZStep
        nzOverZStep = l.nzOverZStep
        tuOverZStep = l.tuOverZStep
        tvOverZStep = l.tvOverZStep

        return this
    }

    fun getLeftX(delta: Float) = leftX + leftXStep * delta
    fun getRightX(delta: Float) = rightX + rightXStep * delta
    fun getZ(delta: Float) = leftZ + leftZStep * delta
    fun get1OverZ(delta: Float) = _1OverZ + _1OverZStep * delta
    fun getNxOverZ(delta: Float) = nxOverZ + nxOverZStep * delta
    fun getNyOverZ(delta: Float) = nyOverZ + nyOverZStep * delta
    fun getNzOverZ(delta: Float) = nzOverZ + nzOverZStep * delta
    fun getTuOverZ(delta: Float) = tuOverZ + tuOverZStep * delta
    fun getTvOverZ(delta: Float) = tvOverZ + tvOverZStep * delta
}
