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

import com.zynaps.parallax.math.Random.nextFloat
import com.zynaps.parallax.math.Scalar.max
import com.zynaps.parallax.math.Scalar.min
import com.zynaps.parallax.math.Scalar.pow
import com.zynaps.parallax.math.Scalar.sqrt
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "DuplicatedCode")
class HeightField(val resX: Int, val resZ: Int) {

    private val heightField: FloatArray = FloatArray(resX * resZ)

    var minHeight = 0.0F
        private set

    var maxHeight = 0.0F
        private set

    fun getItem(x: Int, z: Int): Float {
        return heightField[z * resX + x]
    }

    fun setItem(x: Int, z: Int, value: Float) {
        heightField[z * resX + x] = min(maxHeight, max(minHeight, value))
    }

    fun reset() {
        Arrays.fill(heightField, 127.0F)
        minHeight = MinimumHeight
        maxHeight = MaximumHeight
    }

    fun computeMinMax() {
        var min = Int.MAX_VALUE.toFloat()
        var max = Int.MIN_VALUE.toFloat()
        for (height in heightField) {
            min = min(min, height)
            max = max(max, height)
        }
    }

    fun smooth() {
        val hfcopy = HeightField(resX, resZ)
        System.arraycopy(heightField, 0, hfcopy.heightField, 0, hfcopy.heightField.size)
        for (y in 1 until resZ - 1) {
            for (x in 1 until resX - 1) {
                var accum = 0.0F
                var accumCount = 0
                for (y2 in y - 1..y + 1) {
                    for (x2 in x - 1..x + 1) {
                        accum += hfcopy.getItem(x2, y2)
                        accumCount++
                    }
                }
                setItem(x, y, accum / accumCount)
            }
        }
    }

    fun canyonize() {
        val range = maxHeight - minHeight
        for (y in 0 until resZ) {
            for (x in 0 until resX) {
                val h = getItem(x, y)
                var sh = (h - minHeight) / range
                sh = pow(sh, 2.0F)
                sh = sh * range + minHeight
                setItem(x, y, sh)
            }
        }
    }

    fun fractalize() {
        reset()
        val done = BooleanArray(resX * resZ)

        setItem(0, 0, rangedRandomHeight())
        done[0] = true

        setItem(resX - 1, 0, rangedRandomHeight())
        done[resX - 1] = true

        setItem(resX - 1, resZ - 1, rangedRandomHeight())
        done[(resZ - 1) * resX + resX - 1] = true

        setItem(0, resZ - 1, rangedRandomHeight())
        done[(resZ - 1) * resX] = true

        frac(done, 0, 0, resX - 1, resZ - 1, 8)

        computeMinMax()
    }

    private fun frac(done: BooleanArray, x1: Int, y1: Int, x2: Int, y2: Int, level: Int) {
        val mx = x1 + (x2 - x1 shr 1)
        val my = y1 + (y2 - y1 shr 1)
        val h1 = getItem(x1, y1)
        val h2 = getItem(x2, y1)
        val h3 = getItem(x1, y2)
        val h4 = getItem(x2, y2)
        val xx = x2 - x1
        val yy = y2 - y1
        val scale = sqrt(xx * xx + yy * yy.toFloat()) * 0.79F
        val rh1 = clamp((h1 + h2) * 0.5F + scaledRandomHeight(scale))
        if (!done[y1 * resX + mx]) {
            setItem(mx, y1, rh1)
            done[y1 * resX + mx] = true
        }
        val rh2 = clamp((h3 + h4) * 0.5F + scaledRandomHeight(scale))
        if (!done[y2 * resX + mx]) {
            setItem(mx, y2, rh2)
            done[y2 * resX + mx] = true
        }
        val rh3 = clamp((h1 + h3) * 0.5F + scaledRandomHeight(scale))
        if (!done[my * resX + x1]) {
            setItem(x1, my, rh3)
            done[my * resX + x1] = true
        }
        val rh4 = clamp((h2 + h4) * 0.5F + scaledRandomHeight(scale))
        if (!done[my * resX + x2]) {
            setItem(x2, my, rh4)
            done[my * resX + x2] = true
        }
        val rh5 = clamp((h1 + h2 + h3 + h4) * 0.25F + scaledRandomHeight(scale))
        if (!done[my * resX + mx]) {
            setItem(mx, my, rh5)
            done[my * resX + mx] = true
        }
        if (level > 0) {
            frac(done, x1, y1, mx, my, level - 1)
            frac(done, mx, y1, x2, my, level - 1)
            frac(done, x1, my, mx, y2, level - 1)
            frac(done, mx, my, x2, y2, level - 1)
        }
    }

    companion object {
        private const val MinimumHeight = 0.0F
        private const val MaximumHeight = 255.0F

        private fun rangedRandomHeight(): Float {
            return MinimumHeight + (MaximumHeight - MinimumHeight) * nextFloat()
        }

        private fun scaledRandomHeight(s: Float): Float {
            return -s + 2.0F * s * nextFloat()
        }

        private fun clamp(h: Float): Float {
            return if (h < MinimumHeight) MinimumHeight else min(h, MaximumHeight)
        }
    }

    init {
        reset()
    }
}
