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
import com.zynaps.parallax.system.Parallel
import java.lang.Math.fma

internal class ColorRasterizer : Rasterizer {

    override fun clear(device: Device, color: Int, depth: Float) {
        for (i in 0 until device.colorBuffer.size) device.colorBuffer[i] = color
        for (i in 0 until device.emissiveBuffer.size) device.emissiveBuffer[i] = 0x000000
    }

    override fun render(device: Device) {
        Parallel.execute(device.height) { y ->
            val offset = y * device.width
            val depthBuffer = device.depthBuffer
            val colrArray = device.colorBuffer
            val normArray = device.normalBuffer
            val specArray = device.specularBuffer
            val emitArray = device.emissiveBuffer
            for (buffer in device.spanBuffers) {
                val spanBuffer = buffer[y]
                if (spanBuffer.size == 0) continue
                val yy = y.toFloat()
                for (i in 0 until spanBuffer.size) {
                    val fragment = spanBuffer[i]
                    val diff = fragment.material.diffuse
                    val spec = fragment.material.specular
                    val emit = fragment.material.emissive
                    val glos = fragment.material.glossiness shl 24
                    val delta = yy - fragment.leftY
                    val lx = fragment.getLeftX(delta)
                    val x1 = max(0, ceil(lx))
                    val x2 = min(device.width, ceil(fragment.getRightX(yy - fragment.rightY)))
                    val preStep = x1 - lx
                    var z1 = fma(fragment._zOverZdX, preStep, fragment.getZ(delta))
                    var _1OverZ = fma(fragment._1OverZdX, preStep, fragment.get1OverZ(delta))
                    var nxOverZ = fma(fragment.nxOverZdX, preStep, fragment.getNxOverZ(delta))
                    var nyOverZ = fma(fragment.nyOverZdX, preStep, fragment.getNyOverZ(delta))
                    var nzOverZ = fma(fragment.nzOverZdX, preStep, fragment.getNzOverZ(delta))
                    var tuOverZ = fma(fragment.tuOverZdX, preStep, fragment.getTuOverZ(delta))
                    var tvOverZ = fma(fragment.tvOverZdX, preStep, fragment.getTvOverZ(delta))
                    for (x in offset + x1 until offset + x2) {
                        if (z1 <= depthBuffer[x]) {
                            val oz = 1F / _1OverZ
                            val nz = 511F * oz
                            val tu = tuOverZ * oz
                            val tv = tvOverZ * oz
                            colrArray[x] = diff.sample(tu, tv)
                            specArray[x] = spec.sample(tu, tv) or glos
                            emitArray[x] = emit.sample(tu, tv)
                            val nr = fma(nxOverZ, nz, 512F).toInt()
                            val ng = fma(nyOverZ, nz, 512F).toInt()
                            val nb = fma(nzOverZ, nz, 512F).toInt()
                            normArray[x] = nr.shl(20) or ng.shl(10) or nb
                        }
                        z1 += fragment._zOverZdX
                        _1OverZ += fragment._1OverZdX
                        nxOverZ += fragment.nxOverZdX
                        nyOverZ += fragment.nyOverZdX
                        nzOverZ += fragment.nzOverZdX
                        tuOverZ += fragment.tuOverZdX
                        tvOverZ += fragment.tvOverZdX
                    }
                }
            }
        }
    }
}
