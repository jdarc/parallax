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

import com.zynaps.parallax.core.Color.blu
import com.zynaps.parallax.core.Color.grn
import com.zynaps.parallax.core.Color.red
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.data.Offset.offset
import org.junit.jupiter.api.Test
import java.util.concurrent.ThreadLocalRandom

class ColorTest {

    @Test
    fun shouldBlendBetweenColors() {
        val random = ThreadLocalRandom.current()
        repeat(100) {
            val rgb1 = random.nextInt(0, 0xFFFFFF)
            val rgb2 = random.nextInt(0, 0xFFFFFF)
            val amount = random.nextDouble()

            val result = Color.blend(rgb1, rgb2, (amount * 256.0).toInt())
            val expected = slowBlend(rgb1, rgb2, amount)

            assertThat(red(result)).isCloseTo(red(expected), offset(1))
            assertThat(grn(result)).isCloseTo(grn(expected), offset(1))
            assertThat(blu(result)).isCloseTo(blu(expected), offset(1))
        }
    }

    private fun slowBlend(a: Int, b: Int, amount: Double): Int {
        val invAmount = 1.0 - amount
        val red = red(a) * amount + red(b) * invAmount
        val grn = grn(a) * amount + grn(b) * invAmount
        val blu = blu(a) * amount + blu(b) * invAmount
        return Color.pack(red.toInt(), grn.toInt(), blu.toInt())
    }
}
