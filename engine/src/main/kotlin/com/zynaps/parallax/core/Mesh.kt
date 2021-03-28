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

import com.zynaps.parallax.math.Aabb
import java.util.*

class Mesh(val vertexBuffer: FloatArray, val indexBuffer: IntArray) : Geometry {

    init {
        validate()
    }

    override val vertexCount get() = vertexBuffer.size / 8

    override val triangleCount get() = indexBuffer.size / 3

    override val localBounds = (vertexBuffer.indices step 8).fold(Aabb(), { dst, it ->
        dst.aggregate(vertexBuffer[it], vertexBuffer[it + 1], vertexBuffer[it + 2])
    })

    override fun render(device: Device) {
        device.draw(vertexBuffer, indexBuffer, indexBuffer.size / 3)
    }

    private fun validate() {
        if (vertexBuffer.size % 8 != 0) throw RuntimeException("Vertex buffer is null or has invalid length (must be multiple of 8)")
        if (indexBuffer.size % 3 != 0) throw RuntimeException("Index buffer is null or has invalid length (must be multiple of 3)")
        if (Arrays.stream(indexBuffer).anyMatch { idx -> idx < 0 || idx > vertexBuffer.size shr 3 }) {
            throw RuntimeException("Index buffer is invalid, index references invalid vertex")
        }
    }
}
