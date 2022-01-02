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

import com.zynaps.parallax.core.Color.alpha
import com.zynaps.parallax.core.Color.blu
import com.zynaps.parallax.core.Color.grn
import com.zynaps.parallax.core.Color.red
import com.zynaps.parallax.math.Scalar
import java.awt.image.BufferedImage

class TextureMap private constructor(buffer: RenderBuffer) {
    val width = buffer.width
    val height = buffer.size / width

    private val siz8 = width.shl(8).toFloat() - 256f
    private val bits = Scalar.log2(width.toFloat()).toInt()
    private val mask = width * height - 1
    private val data = pack(buffer)

    fun sample(u: Float, v: Float): Int {
        val x = (u * siz8).toInt()
        val y = (v * siz8).toInt()
        val bx = x and 0xFF
        val by = y and 0xFF
        val m = y.ushr(8) shl bits or x.ushr(8)
        val p1 = data[mask and m]
        val p2 = data[mask and m + 1]
        val weight1 = (256 - bx) * (256 - by) ushr 8
        val weight4 = bx * by ushr 8
        val weight3 = by - weight4
        val weight2 = bx - weight4
        val n1 = (p1 shr 0 and MASK) * weight1
        val n3 = (p1 shr 8 and MASK) * weight3
        val n2 = (p2 shr 0 and MASK) * weight2
        val n4 = (p2 shr 8 and MASK) * weight4
        val sum1 = n1 + n2 ushr 8
        val sum2 = n3 + n4 ushr 8
        val sum = sum1 + sum2
        return (sum.ushr(24) and 0xFF00FF00 or (sum and 0xFF00FF)).toInt()
    }

    private fun pack(pixels: RenderBuffer): LongArray {
        val data = pixels.data.map(Int::toLong).map { alpha(it).shl(48) or grn(it).shl(32) or red(it).shl(16) or blu(it) }
        return data.mapIndexed { index, value -> value or data[mask and index + width].shl(8) }.toLongArray()
    }

    companion object {
        private const val MASK = 0b11111111000000001111111100000000111111110000000011111111

        @Throws(IllegalArgumentException::class)
        fun create(image: BufferedImage): TextureMap {
            val data = image.getRGB(0, 0, image.width, image.height, null, 0, image.height)
            return create(RenderBuffer.wrap(data, image.width))
        }

        @Throws(IllegalArgumentException::class)
        fun create(buffer: RenderBuffer): TextureMap {
            if (buffer.width != buffer.height || !Scalar.isPot(buffer.width)) {
                throw IllegalArgumentException("dimensions not supported")
            }
            return TextureMap(buffer)
        }
    }
}
