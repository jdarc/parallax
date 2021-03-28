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

object Samplers {

    class Color(private val rgb: Int) : Sampler {
        override fun sample(u: Float, v: Float) = rgb

        companion object {
            val RED = Color(0xFF0000)
            val GREEN = Color(0x00FF00)
            val BLUE = Color(0x0000FF)
            val WHITE = Color(0xFFFFFF)
            val BLACK = Color(0x000000)
        }
    }

    class Checker(private val rgb1: Int, private val rgb2: Int, private val scale: Float = 16.0F) : Sampler {
        override fun sample(u: Float, v: Float): Int {
            val i = (u * scale).toInt() + (v * scale).toInt() and 1
            return i * rgb1 or (1 - i) * rgb2
        }
    }

    class Texture(private val map: TextureMap) : Sampler {
        override fun sample(u: Float, v: Float) = map.sample(u, v)
    }
}
