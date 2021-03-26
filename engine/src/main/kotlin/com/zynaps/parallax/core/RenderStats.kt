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

class RenderStats {
    private var beginTime = 0L
    private var endTime = 0L

    var vertices = 0
    var triangles = 0
    var rendered = 0
    var clipped = 0
    var culled = 0
    var shaded = 0
    var overdraw = 0
    var spans = 0
    val nanoseconds get() = endTime - beginTime

    fun start() {
        vertices = 0
        triangles = 0
        rendered = 0
        clipped = 0
        culled = 0
        shaded = 0
        overdraw = 0
        spans = 0
        beginTime = System.nanoTime()
        endTime = beginTime
    }

    fun stop() {
        endTime = System.nanoTime()
    }
}
