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

class RenderBuffer private constructor(val data: IntArray, val width: Int) {
    val size = data.size
    val height = if (width == 0) 0 else size / width

    operator fun get(index: Int) = data[index]
    operator fun set(index: Int, value: Int) {
        data[index] = value
    }

    fun fill(value: Int) {
        for (i in data.indices) data[i] = value
    }

    fun copyInto(dst: IntArray, offset: Int = 0, start: Int = 0, end: Int = data.size) {
        data.copyInto(dst, offset, start, end)
    }

    companion object {
        val ZERO = RenderBuffer(IntArray(1), 0)
        fun wrap(data: IntArray, scan: Int) = RenderBuffer(data, scan)
        fun create(width: Int, height: Int) = wrap(IntArray(width * height), width)
    }
}
