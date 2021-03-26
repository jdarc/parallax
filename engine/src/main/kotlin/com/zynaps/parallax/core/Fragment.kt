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

import java.lang.Math.fma

@Suppress("PropertyName", "PrivatePropertyName", "DuplicatedCode")
internal class Fragment {

    var leftX = 0.0F
    private var rightX = 0.0F
    private var leftZ = 0.0F

    private var leftXStep = 0.0F
    private var rightXStep = 0.0F
    private var leftZStep = 0.0F

    var _1OverZdX = 0.0F
    var _zOverZdX = 0.0F
    var nxOverZdX = 0.0F
    var nyOverZdX = 0.0F
    var nzOverZdX = 0.0F
    var tuOverZdX = 0.0F
    var tvOverZdX = 0.0F

    private var _1OverZ = 0.0F
    private var nxOverZ = 0.0F
    private var nyOverZ = 0.0F
    private var nzOverZ = 0.0F
    private var tuOverZ = 0.0F
    private var tvOverZ = 0.0F

    private var _1OverZStep = 0.0F
    private var nxOverZStep = 0.0F
    private var nyOverZStep = 0.0F
    private var nzOverZStep = 0.0F
    private var tuOverZStep = 0.0F
    private var tvOverZStep = 0.0F

    var leftY = 0
    var rightY = 0

    var material = Material.DEFAULT

    var next: Fragment? = null

    fun configure(m: Material, g: Gradients, l: Edge, r: Edge): Fragment {
        rightY = r.y1
        rightX = r.x
        rightXStep = r.xStep

        leftY = l.y1
        leftX = l.x
        leftXStep = l.xStep

        leftZ = l.z
        leftZStep = l.zStep

        _1OverZdX = g._1OverZdX
        _zOverZdX = g._zOverZdX
        nxOverZdX = g.nxOverZdX
        nyOverZdX = g.nyOverZdX
        nzOverZdX = g.nzOverZdX
        tuOverZdX = g.tuOverZdX
        tvOverZdX = g.tvOverZdX

        _1OverZ = l._1OverZ
        nxOverZ = l.nxOverZ
        nyOverZ = l.nyOverZ
        nzOverZ = l.nzOverZ
        tuOverZ = l.tuOverZ
        tvOverZ = l.tvOverZ

        _1OverZStep = l._1OverZStep
        nxOverZStep = l.nxOverZStep
        nyOverZStep = l.nyOverZStep
        nzOverZStep = l.nzOverZStep
        tuOverZStep = l.tuOverZStep
        tvOverZStep = l.tvOverZStep

        material = m
        return this
    }

    fun getLeftX(delta: Float) = fma(leftXStep, delta, leftX)
    fun getRightX(delta: Float) = fma(rightXStep, delta, rightX)
    fun getZ(delta: Float) = fma(leftZStep, delta, leftZ)
    fun get1OverZ(delta: Float) = fma(_1OverZStep, delta, _1OverZ)
    fun getNxOverZ(delta: Float) = fma(nxOverZStep, delta, nxOverZ)
    fun getNyOverZ(delta: Float) = fma(nyOverZStep, delta, nyOverZ)
    fun getNzOverZ(delta: Float) = fma(nzOverZStep, delta, nzOverZ)
    fun getTuOverZ(delta: Float) = fma(tuOverZStep, delta, tuOverZ)
    fun getTvOverZ(delta: Float) = fma(tvOverZStep, delta, tvOverZ)
}