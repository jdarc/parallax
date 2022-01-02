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
import com.zynaps.parallax.system.DynamicArray
import com.zynaps.parallax.system.DynamicPool

internal class SpanBuffer(val width: Int, val height: Int) {
    private val pool = DynamicPool(128) { Fragment() }
    private val spans = Array<DynamicArray<Fragment>>(height) { DynamicArray(128) }

    operator fun get(index: Int) = spans[index]

    fun add(material: Material, gradients: Gradients, left: Edge, right: Edge, leader: Edge) {
        val fragment = pool.next().configure(material, gradients, left, right)
        var lx = fragment.getLeftX(leader.y1.toFloat() - fragment.leftY)
        var rx = fragment.getRightX(leader.y1.toFloat() - fragment.rightY)
        for (y in leader.y1 until min(height, leader.y2)) {
            val x1 = max(0, ceil(lx))
            if (x1 < width) {
                val x2 = min(width, ceil(rx))
                if (x2 > x1) spans[y].add(fragment)
            }
            lx += fragment.leftXStep
            rx += fragment.rightXStep
        }
    }

    fun reset() {
        pool.reset()
        spans.forEach { it.reset() }
    }
}
