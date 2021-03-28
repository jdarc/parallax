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

internal class Clipper {

    private val dst = Array(6) { Vertex() }
    private val src = Array(6) { Vertex() }

    val a = src[0]
    val b = src[1]
    val c = src[2]
    val d = src[3]
    val e = src[4]

    fun clip(a: Vertex, b: Vertex, c: Vertex): Int {
        val mask = computeClipMask(a, b, c)

        if (outside(mask)) return 0

        src[0].set(a)
        src[1].set(b)
        src[2].set(c)
        if (inside(mask)) return 3

        src[3].set(a)
        return clip(dst, src, clip(src, dst, 3, -1.0F), 1.0F)
    }

    internal companion object {
        private const val NER = 0b000000000000000111
        private const val FAR = 0b000000000000111000
        private const val BOT = 0b000000000111000000
        private const val TOP = 0b000000111000000000
        private const val LFT = 0b000111000000000000
        private const val RGH = 0b111000000000000000

        private const val SAFETY = 0.999995F

        private fun inside(mask: Int) = mask == 0

        private fun outside(packed: Int) = (packed and NER == NER) or (packed and FAR == FAR) or (packed and BOT == BOT) or
                (packed and TOP == TOP) or (packed and LFT == LFT) or (packed and RGH == RGH)

        private fun computeClipMask(v0: Vertex, v1: Vertex, v2: Vertex): Int {
            var acc = if (v2.vx < v2.vw) 0 else 1
            acc = acc shl 1 or if (v1.vx < v1.vw) 0 else 1
            acc = acc shl 1 or if (v0.vx < v0.vw) 0 else 1
            acc = acc shl 1 or if (v2.vx > -v2.vw) 0 else 1
            acc = acc shl 1 or if (v1.vx > -v1.vw) 0 else 1
            acc = acc shl 1 or if (v0.vx > -v0.vw) 0 else 1
            acc = acc shl 1 or if (v2.vy < v2.vw) 0 else 1
            acc = acc shl 1 or if (v1.vy < v1.vw) 0 else 1
            acc = acc shl 1 or if (v0.vy < v0.vw) 0 else 1
            acc = acc shl 1 or if (v2.vy > -v2.vw) 0 else 1
            acc = acc shl 1 or if (v1.vy > -v1.vw) 0 else 1
            acc = acc shl 1 or if (v0.vy > -v0.vw) 0 else 1
            acc = acc shl 1 or if (v2.vz < v2.vw) 0 else 1
            acc = acc shl 1 or if (v1.vz < v1.vw) 0 else 1
            acc = acc shl 1 or if (v0.vz < v0.vw) 0 else 1
            acc = acc shl 1 or if (v2.vz > -v2.vw) 0 else 1
            acc = acc shl 1 or if (v1.vz > -v1.vw) 0 else 1
            return acc shl 1 or if (v0.vz > -v0.vw) 0 else 1
        }

        private fun clip(src: Array<Vertex>, dst: Array<Vertex>, remaining: Int, side: Float): Int {
            var ia = 0
            var ib = 0
            var a1 = src[ia++]
            var a2 = src[ia++]
            var na = a1.vz * side - a1.vw * SAFETY
            for (it in 0 until remaining) {
                val nb = a2.vz * side - a2.vw * SAFETY
                if (na < 0.0F) {
                    if (nb < 0.0F) {
                        dst[ib++].set(a2)
                    } else {
                        dst[ib++].lerp(a1, a2, na / (na - nb))
                    }
                } else if (nb < 0.0F) {
                    dst[ib++].lerp(a1, a2, na / (na - nb))
                    dst[ib++].set(a2)
                }
                na = nb
                a1 = a2
                a2 = src[ia++]
            }
            dst[ib].set(dst[0])
            return ib
        }
    }
}
