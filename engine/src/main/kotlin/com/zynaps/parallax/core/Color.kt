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

import com.zynaps.parallax.math.Random
import com.zynaps.parallax.math.Scalar.clamp

@Suppress("unused")
object Color {

    fun alpha(rgb: Int) = rgb shr 24 and 0xFF

    fun alpha(rgb: Long) = alpha(rgb.toInt()).toLong()

    fun red(rgb: Int) = rgb shr 16 and 0xFF

    fun red(rgb: Long) = red(rgb.toInt()).toLong()

    fun grn(rgb: Int) = rgb shr 8 and 0xFF

    fun grn(rgb: Long) = grn(rgb.toInt()).toLong()

    fun blu(rgb: Int) = rgb and 0xFF

    fun blu(rgb: Long) = blu(rgb.toInt()).toLong()

    fun fltRed(rgb: Int) = red(rgb) * 0.00390625F

    fun fltGrn(rgb: Int) = grn(rgb) * 0.00390625F

    fun fltBlu(rgb: Int) = blu(rgb) * 0.00390625F

    fun fastPack(red: Int, grn: Int, blu: Int) = red.shl(16) or grn.shl(8) or blu

    fun pack(red: Int, grn: Int, blu: Int) = fastPack(clamp(red, 0, 255), clamp(grn, 0, 255), clamp(blu, 0, 255))

    fun explode(v: Int): Long {
        val l = v.toLong()
        return l and 0xFF00FF00 shl 24 or (l and 0x00FF00FF)
    }

    fun pack(red: Float, grn: Float, blu: Float): Int {
        val r = clamp((red * 256.0F).toInt(), 0, 255)
        val g = clamp((grn * 256.0F).toInt(), 0, 255)
        val b = clamp((blu * 256.0F).toInt(), 0, 255)
        return fastPack(r, g, b)
    }

    fun mul(rgb: Int, s256: Int): Int {
        val red = (rgb and 0xFF0000) * s256 ushr 8 and 0xFF0000
        val grn = (rgb and 0x00FF00) * s256 ushr 8 and 0x00FF00
        val blu = (rgb and 0x0000FF) * s256 ushr 8 and 0x0000FF
        return red or grn or blu
    }

    fun blend(rgb1: Int, rgb2: Int, amount: Int): Int {
        val mix1 = 0xFF00FF.and(rgb1) * amount + 0xFF00FF.and(rgb2) * (256 - amount) shr 8 and 0xFF00FF
        val mix2 = 0x00FF00.and(rgb1) * amount + 0x00FF00.and(rgb2) * (256 - amount) shr 8 and 0x00FF00
        return mix1 or mix2
    }

    fun blend(rgb1: Int, rgb2: Int): Int {
        val sum = rgb1 + rgb2
        val carries = sum - (rgb1 xor rgb2 and 0x10101) and 0x1010100
        return sum - carries or carries - (carries ushr 8)
    }

    fun add(rgb1: Int, rgb2: Int) = when {
        rgb1 == 0 -> rgb2
        rgb2 == 0 -> rgb1
        else -> pack(red(rgb1) + red(rgb2), grn(rgb1) + grn(rgb2), blu(rgb1) + blu(rgb2))
    }

    fun random(min: Int = 0): Int {
        val max = 0xFF - min
        return pack(Random.nextInt(min, min + max), Random.nextInt(min, min + max), Random.nextInt(min, min + max))
    }
}
